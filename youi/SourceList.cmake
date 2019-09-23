# =============================================================================
# © You i Labs Inc. 2000-2019. All rights reserved.
if (IOS OR TVOS OR OSX)
    file(GLOB_RECURSE YI_PROJECT_SOURCE "src/*.cpp" "src/*.mm")
else()
    file(GLOB_RECURSE YI_PROJECT_SOURCE "src/*.cpp")
endif()

file(GLOB_RECURSE YI_PROJECT_HEADERS "src/*.h")
