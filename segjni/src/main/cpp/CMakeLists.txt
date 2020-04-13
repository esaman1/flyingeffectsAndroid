

cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_AUTOMOC ON)
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_INCLUDE_CURRENT_DIR ON)

include_directories(${CMAKE_SOURCE_DIR}/include)


#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -fexceptions -frtti -flto -fuse-ld=gold -Wl,-O3 -fvisibility=hidden -DMGFACEAPI_LIBRARY")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}  -DMG_FACE_UNLOCK_LIBRARY -Wall -std=gnu++11 -fexceptions -fuse-ld=gold -fvisibility=hidden -Wl,--version-script=${CMAKE_SOURCE_DIR}/symbols.script")


set(jnilibs "${CMAKE_SOURCE_DIR}/../jniLibs")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${jnilibs}/${ANDROID_ABI})
set(sdkdir "${CMAKE_SOURCE_DIR}/../../../../android_sdk/jni/")

include_directories(${sdkdir}/src)


#add_library(cvcore STATIC IMPORTED )
#set_target_properties(cvcore PROPERTIES
#    IMPORTED_LOCATION "${sdkdir}/libs/${ANDROID_ABI}/libopencv_core.a")
#
#add_library(cv STATIC IMPORTED )
#set_target_properties(cv PROPERTIES
#    IMPORTED_LOCATION "${sdkdir}/libs/${ANDROID_ABI}/libopencv_imgproc.a")
#
#add_library(bz STATIC IMPORTED )
#set_target_properties(bz PROPERTIES
#    IMPORTED_LOCATION "${sdkdir}/libs/${ANDROID_ABI}/libz.a")


add_library(megviisegsdk SHARED IMPORTED )
set_target_properties(megviisegsdk PROPERTIES
    IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libmegsegment.so")

add_library(face_new SHARED IMPORTED )
set_target_properties(face_new PROPERTIES
    IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libmegface-new.so")

#add_library(stl SHARED IMPORTED )
#set_target_properties(stl PROPERTIES
#    IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libgnustl_shared.so")


add_library(MegviiSegJni
         SHARED
         megvii_seg_jni_new.cpp
#         ${sdkdir}/src/SEG_Megvii_Api.cpp
#         ${sdkdir}/src/MG_segment_handler.cpp
#         ${sdkdir}/src/mghum_debug_dumper.cpp
        )


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

find_library( GLESv2-lib GLESv2 )
find_library( EGL-lib EGL )

find_library(jni-graphics  jnigraphics)
#find_library( libz z )



#target_link_libraries(MegviiSegJni face_new cv cvcore  bz stl
#                       ${log-lib} ${GLESv2-lib} ${EGL-lib} android ${jni-graphics})



target_link_libraries(MegviiSegJni megviisegsdk  face_new
                       ${log-lib} ${GLESv2-lib} ${EGL-lib} android ${jni-graphics})




