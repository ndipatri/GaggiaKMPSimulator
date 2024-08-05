
enum class CommandType(val transmitName: String) {
    FIRST_BUTTON_CLICK("short"),
    SECOND_BUTTON_CLICK("long");

    companion object {
        fun byTransmitName(transmitName: String): CommandType {
            values().forEach {
                if (it.transmitName == transmitName) {
                    return it
                }
            }
            return FIRST_BUTTON_CLICK
        }
    }
}

const val commandTopic = "ndipatri/feeds/robogaggiacommand"
const val telemetryTopic = "ndipatri/feeds/robogaggiatelemetry"

enum class GaggiaState(
    val stateName: String,
    val videoStartSeconds: Int? = null,
    val videoEndSeconds: Int? = null
) {
    IGNORING_NETWORK("ignoringNetwork"),
    SLEEP("sleep"),
    JOINING_NETWORK("joiningNetwork", videoStartSeconds = 0, videoEndSeconds = 12),
    PREHEAT("preheat", videoStartSeconds = 14, videoEndSeconds = 22),
    MEASURE_BEANS("measureBeans", videoStartSeconds = 28, videoEndSeconds = 40),
    TARE_CUP_AFTER_MEASURE("tareCupAfterMeasure", videoStartSeconds = 44, videoEndSeconds = 126),
    HEATING_TO_BREW("heating", videoStartSeconds = 130, videoEndSeconds = 1),

    PREINFUSION("preInfusion", videoStartSeconds = 131, videoEndSeconds = 155),
    BREWING("brewing", videoStartSeconds = 159, videoEndSeconds = 177),

    DONE_BREWING("doneBrewing", videoStartSeconds = 179, videoEndSeconds = 181),
    HEATING_TO_STEAM("heatingToSteam", videoStartSeconds = 191, videoEndSeconds = 223),
    STEAMING("steaming", videoStartSeconds = 227, videoEndSeconds = 280),
    CLEAN_GROUP_READY("cleanGroupReady", videoStartSeconds = 281, videoEndSeconds = 289),
    CLEAN_GROUP_DONE("cleanGroupDone", videoStartSeconds = 291, videoEndSeconds = 292),

    CLEAN_OPTIONS("cleanOptions"),
    DESCALE("descale"),
    COOL_START("coolStart"),
    COOLING("cooling"),
    COOL_DONE("coolDone"),
    BACKFLUSH_INSTRUCTION_1("cleanInst1"),
    BACKFLUSH_INSTRUCTION_2("cleanInst2"),
    BACKFLUSH_CYCLE_1("cleanSoap"),
    BACKFLUSH_INSTRUCTION_3("cleanInst3"),
    BACKFLUSH_CYCLE_2("cleanRinse"),
    BACKFLUSH_CYCLE_DONE("cleanDone"),
    HEATING_TO_DISPENSE("heatingToDispense"),
    DISPENSE_HOT_WATER("dispenseHotWater"),
    NA("na");

    companion object {
        fun byName(name: String): GaggiaState {
            for (candidate in GaggiaState.values()) {
                if (candidate.stateName == name) {
                    return candidate
                }
            }

            return NA
        }
    }
}

data class Weight(val currentWeight: Float, val targetWeight: Float?) {
    constructor(weight: String) : this(
        weight.trim().split(":")[0].toFloat(),
        if (weight.trim().split(":").size == 2) weight.trim().split(":")[1].toFloat() else null
    )
}

data class TelemetryMessage(
    val state: GaggiaState,

    // of form: <currentWeight>:<targetWeight>
    val weight: Weight,

    val pressureBars: String,
    val dutyCyclePercent: String,
    val flowRateGPS: String,
    val brewTempC: String,
    val shotsUntilBackflush: String,
    val totalShots: String,
    val boilerState: String
)

fun renderTelemetry(rawTelemetryString: String): List<TelemetryMessage> {
    return rawTelemetryString.split("\n").map { line ->
        line.split(",").let {
            TelemetryMessage(
                state = GaggiaState.byName(it[0]),
                weight = Weight(it[1]),
                pressureBars = it[2],
                dutyCyclePercent = it[3],
                flowRateGPS = it[4],
                brewTempC = it[5],
                shotsUntilBackflush = it[6],
                totalShots = it[7],
                boilerState = it[8],
            )
        }
    }
}

val typicalPreinfusionCycleTelemetryString =
    "preInfusion, 0:34, -1.000000, 40.400000, 0.000000, 105.750000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 105.000000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 104.750000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 104.250000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 104.250000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 103.250000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 103.000000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 101.500000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 100.750000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 100.000000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 0.000000, 40.400000, 0.000000, 100.500000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 1.000000, 40.400000, 0.000000, 101.500000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 1.000000, 40.400000, 0.000000, 104.000000, 2, 2235, 0\n" +
    "preInfusion, 0:34, 1.000000, 40.400000, 0.000000, 106.250000, 2, 2235, 0\n" +
    "preInfusion, 1:34, 2.000000, 40.400000, 0.833333, 109.250000, 2, 2235, 0"

val typicalBrewCycleTelemetryString =
    "brewing, 2:34, 2.000000, 41.266667, 0.833333, 110.250000, 2, 2235, 0\n" +
    "brewing, 4:34, 2.000000, 43.000000, 1.666667, 111.750000, 2, 2235, 0\n" +
    "brewing, 5:34, 2.000000, 43.900000, 0.833333, 111.750000, 2, 2235, 0\n" +
    "brewing, 7:34, 4.000000, 45.800000, 1.666667, 111.000000, 2, 2235, 0\n" +
    "brewing, 9:34, 4.000000, 46.700000, 1.666667, 110.250000, 2, 2235, 0\n" +
    "brewing, 11:34, 4.000000, 47.766667, 1.666667, 109.000000, 2, 2235, 0\n" +
    "brewing, 14:34, 5.000000, 48.833333, 2.500000, 107.500000, 2, 2235, 0\n" +
    "brewing, 16:34, 5.000000, 49.066667, 1.666667, 106.000000, 2, 2235, 0"