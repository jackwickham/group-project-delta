# The packet structure for the communications system
All numbers are in bytes (the order is actually dependent on Java int byte
order, I'm assuming big-endianess for this document):

**Header:**


1: The type of message being sent.  
2-4: The length of the message, so theoretical max length 2^24 ~ 17 mil.  
5-8: The platoon id.  
9-12: The vehicle id.  
13+: The data for the specific packets

#### Message types
There are currently at most 256 possible message types as only 1 byte is assigned to them.

0: Emergency stop  
1: Normal data message  
2: Request to merge (Sent by leader of merging platoon)  
3: Accept to merge (Reply by leader of main platoon)  
4: Confirm merge (Sent by all members of both platoons)  
5: Merge Complete (Sent by leader of merging platoon)  

---
##### 0. Emergency stop
This tells each vehicle to emergency stop. This has no payload, 
although a reason could be added at a later time.

---
##### 1. Normal data message
This is the normal data which is sent to each other vehicle to coordinate the algorithm.

##### Payload:
As defined in the MessageData class.

---
##### 2. Request to merge
This is used by a smaller platoon to try to merge to a larger platoon.

######Payload:

13-16: Transaction id, generated at random (hope no clash)  
17-20: The platoon id for the merging platoon  
21: blank (For ease of coding)  
22-24: The length of the merging platoon  
25+: An ordered list of the ids of the members of the merging platoon

---
##### 3. Accept to merge
This is used to confirm by the leader that the smaller platoon can merge onto the main platoon.

######Payload:

13-16: Transaction id, same as in the RTM  
17: The boolean accept / reject for the request, if reject then no more payload  
18-20: The length of the main platoon  
21-x: An ordered list of the ids of the members of the main platoon  
x-x+3: The number of ids which need to be replaced  
x+4-end: A list of (old\_id, new\_id) telling vehicle `old_id` in the merging platoon its new id is `new_id`

---
##### 4. Confirm merge
This is sent by every member of both platoons to confirm they have the new information
and are ready to commit it.

######Payload:

13-16: Transaction id

---
##### 5. Merge Complete
This is sent by the leader of the merging platoon after it has seen that all of the 
members have confirmed the merge. (Theoretically we should have a whole distributed
systems retry when packets lost thing. Might do this later but this should suffice
for now)

######Payload:

13-16: Transaction id
