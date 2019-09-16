package com.example.prototype1

enum class Tags{
    DEFAULT
}

val TAG=Tags.DEFAULT.name //for debug

enum class VariableNames{
    NODE
}

enum class NodeTypes{
    NODE,
    BINARY_NODE,
    WEAK_NODE,
    TREE_NODE,
    TIME_NODE,//TODO
    MAP_NODE,//TODO
    QUICK_CREATE_NODE
}