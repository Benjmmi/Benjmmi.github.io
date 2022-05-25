cmake_minimum_required(VERSION 3.0)

project(bsd_lite_d LANGUAGES C)

add_executable(bsd_lite_d main.c)

install(TARGETS bsd_lite_d RUNTIME DESTINATION bin)
