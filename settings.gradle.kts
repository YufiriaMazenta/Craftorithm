rootProject.name = "Craftorithm"
include("core")
include(
    "hook:azureflow",
    "hook:craftengine",
    "hook:ecoitems",
    "hook:executableitems",
    "hook:itemsadder",
    "hook:mmoitems",
    "hook:mythicmobs",
    "hook:neigeitems",
    "hook:nexo",
    "hook:oraxen",
    "hook:sx-item"
)
include(
    "compat:recipe_register_26_1"
)