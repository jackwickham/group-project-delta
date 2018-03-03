# Wireshark Plugin

This is a wireshark plugin written in Lua which can parse the packet structure used to send data over the network.

In order to use the plugin you must run Wireshark with the Lua file as a parameter.
On the command line this involves the command (assuming Wireshark is on the PATH):

`wireshark -X lua_script:platoon-dissector.lua`


There is a Wireshark colours file included, this will highlight the different packet types in different colours.
To use this, open Wireshark then navigate to `View > Coloring Rules` then select
`Import` and choose the `colours.col` file in this directory.
This may duplicate some colour rules, which should then be removed, ensure the
new rules are at the top of the rule list, so they are inspected first.
