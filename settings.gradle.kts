plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "Craftorithm"

include("core")
include("hook")
include(
    "nms:v1_20",
    "nms:v1_20_2",
    "nms:v1_20_3",
    "nms:v1_20_5",
    "nms:v1_21",
    "nms:v1_21_3",
    "nms:v1_21_5",
    "nms:v1_21_7",
    "nms:v1_21_10_spigot",
    "nms:v1_21_10_paper",
    "nms:v1_21_11_spigot",
    "nms:v26_1_spigot",
    "nms:v26_1_paper",
)