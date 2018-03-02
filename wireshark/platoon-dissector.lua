print("Registering platoon dissector")


local PORT_NUMBER = 5187
local PACKET_LENGTH = 200

-- creates a Proto object, but doesn't register it yet
local platoon = Proto("platoon","Platoon Merge Protocol")


-- Message types lookup
local types = {
    [0] = "Emergency",
    [1] = "Data",
    [2] = "Request To Merge",
    [3] = "Accept To Merge",
    [4] = "Merge Confirmation",
    [5] = "Merge Commit",
    [6] = "Beacon ID Question",
    [7] = "Beacon ID Answer"
}

-- create the fields
local pf_type_field = ProtoField.uint8("platoon.type", "Message Type", base.DEC, types)


-- Register the fields
platoon.fields = {pf_type_field}


-- Find the value of some of the fields, so they can be used in the diessector
local types_field = Field.new("platoon.type")


-- Actually assign the dissector
function platoon.dissector(tvbuf, pktinfo, root)
    -- set the protocol column to show our protocol name
    pktinfo.cols.protocol:set("Platoon Merge")

    -- Find out the packet size
    local pktlen = tvbuf:reported_length_remaining()

    if pktlen ~= PACKET_LENGTH then
        return
    end

    -- Add the protocol to the protocol trees
    local tree = root:add(platoon, tvbuf:range(0,pktlen))

    tree:add(pf_type_field, tvbuf:range(0,1))
    return 1
end

-- Listen to UDP traffic on the port specified by PORT_NUMBER
DissectorTable.get("udp.port"):add(PORT_NUMBER, platoon)

-- Finished
