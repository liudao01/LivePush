
cmake_minimum_required(VERSION 3.4.1)
file(GLOB my_source src/main/cpp/*.cpp src/main/cpp/*.c)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")
add_library( # Sets the name of the library.
        push

        # Sets the library as a shared library.
        SHARED

        src/main/cpp/push.cpp

        src/main/cpp/librtmp/amf.c
        src/main/cpp/librtmp/hashswf.c
        src/main/cpp/librtmp/log.c
        src/main/cpp/librtmp/parseurl.c
        src/main/cpp/librtmp/rtmp.c
        ${my_source}
        )


find_library( # Sets the name of the path variable.
        log-lib

        log)


target_link_libraries( # Specifies the target library.
        push

        ${log-lib})