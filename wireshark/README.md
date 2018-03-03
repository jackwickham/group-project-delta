# Wireshark Plugin

This is a wireshark plugin written in Lua which can parse the packet structure used to send data over the network.

In order to use the plugin you must run Wireshark with the Lua file as a parameter.
On the command line this involves the command (assuming Wireshark is on the PATH):

`wireshark -X lua_script:platoon-dissector.lua`
