# TODO

 * [ ] A server is identified by an unique integer that never change
  
 * [x] The servers are called "peers"

 * [ ] The backup service generate an identifier for each file backs up
    * [ ] Id obtained by applying SHA256 to some *bit string* (the bit string should be unique so that the id is unique)
    * [ ] Include data and or metadata to the bit string used to generate a file id (file name, date modified, owner, ...)

 * [ ] Splits each file in chunks
    * [ ] Identify each chunk by the pair (field, chunkNo)
    * [ ] The size of each chunk is 64KByte (where K stands for 1000)
    * [ ] The size of the last chunk is always shorter than that size
    * [ ] If the file size is a multiple of the chunk size, the last chunk has size 0

 * [ ] Back up each chunk independently
    * [ ] A peer need not store all chunks of a file, or even any chunk of a file
    * [ ] The recovery of each chunk is also performed independently of the recovery of other chunks of a file

 * [ ] The service backs up each chunk with a given degree of replication, i.e. on a given number of peers
    * [ ] The desired replication degree of a chunk depends on the file to which it belongs, and all chunks of a given file have the same desired replication degree
      
 * [ ]  The backup service must provide the functionality for reclaiming disk space on peers
    * [ ] Each peer retains total control on the use of its local disk
  
 * [ ] If a server's administrator decides to reduce the amount of local disk space used by the backup service, it may have to free disk space used for backing up chunks
    * [ ] This will decrease the replication degree of the chunk, which may drop below the desired value
       * [ ] The service will try to create new copies of the chunk so as to keep the desired replication degree

 * [ ] A file may be deleted
    * [ ] The backup service should delete all the chunks of that file
    * [ ] deletion of the chunks of a file, may happen not only when the file is deleted on its file system, but also when it is modified, because, for the backup system, it will be a different file

## Message Format and Field Encoding

**Header**

 * [ ] **\<MessageType\> \<Version\> \<SenderId\> \<FileId\> \<ChunkNo\> \<ReplicationDeg\> \<CRLF\>**
    * [ ] Some of these fields may not be used by some messages, but all fields that appear in a message must appear in the relative order specified above

 * [ ] **\<CRLF\>** - '0xD''0xA'
  
 * [ ] Each header line is a sequence of fields, sequences of ASCII codes separated by spaces, the ASCII char ' '
    * [ ] There may be more than one space between fields
    * [ ] There may be zero or more spaces after the last field in a line
    * [ ] The header always terminates with an empty header line
       * [ ] the \<CRLF\> of the last header line is followed immediatly by another \<CRLF\>, without any character in between

 * [ ] **\<MessageType\>**
    * [ ] Each subprotocol specifies its own message types
    * [ ] This fields determines the format of the message and what actions its receivers should perform
    * [ ] This is encoded as a variable length sequance of ASCII characters
      
 * [ ] **\<Version\>**
    * [ ] This is the version of the protocol
    * [ ] Is a 3 ASCII char sequence with the format \<n\>'.'\<m\>, where \<n\> and \<m\> are the ASCII codes of digits
      
 * [ ] **\<SenderId\>**
    * [ ] This is the id of the server that has sent the message
    * [ ] This is encoded as a variable length sequence of ASCII digits
      
 * [ ] **\<FileId\>**
    * [ ] This is the file identifier for the backup service
    * [ ] Obtained by using the SHA256 cryptographic hash function
    * [ ] As its name indicates its lenght is 256 bit
    * [ ] Should be encoded as a 64 ASCII character sequence
    * [ ] The encoding is: each byte of the hash value is encoded by the two ASCII characters corresponding to the hexadeciaml representation of that byte
    * [ ] The entire hash is represented in big-endian order, i.e. from the MSB (byte 31) to LSB (byte 0)

 * [ ] **\<ChunkNo\>**
    * [ ] This field together with the FileId specifies a chunk in the file
    * [ ] The chunk numbers are integers and should be assigned sequentially starting at 0
    * [ ] It is encoded as a sequence of ASCII characters corresponding to the decimal representation of that number, with the most significant digit first
    * [ ] The length of this field is variable, but should not be larger than 6 chars
       * [ ] Each file can have at most one million chunks
       * [ ] Given that each chunk is 64kByte, this limits the size of the files to backup to 64GByte

 * [ ] **\<ReplicationDeg\>**
    * [ ] This field contains the desired replication degree of the chunk
    * [ ] This is a digit, thus allowing a replication degree of up to 9
    * [ ] It takes one byte, which is the ASCII code of that digit

**Body**

 * [ ] The body contains the data of a file chunk
  
 * [ ] The length of the body is variable
  
 * [ ] If it is smaller than the maximum chunk size, 64 KByte, it is the last chunk in a file
  
 * [ ] The protocol does not interpret the contents of the Body
  
 * [ ] For the protocol its value is just a byte sequence

## Subprotocols

 * [x] Many of these subprotocols are initiated by a peer, we call that peer the initiator-peer
  
 * [x] The role of initiator-peer can be played by any peer, depending on the particular instance of the subprotocol
  
 * [ ] **Multicast Control channel (MC)** - used for control messages
    * [ ] All peers must subscribe the MC channel
  
 * [ ] Some subprotocols use also one of two multicast data channels
    * [ ] **Multicast Data Backup channel (MDB)** - used to backup file chunk data
    * [ ] **Multicast Data Restore channel (MDR)** - used to restore file chunk data

 * [ ] The IP multicast addresses of these channels should be configurable
    * [ ] 6 command line arguments of the server program
       * [ ] MC, MDB, MDR, with IP multicast address of each channel before the respective port number
       * [ ] These arguments must follow immediately the first command line argument, which is the server id

### 1. Chunk backup

 * [ ] The initiator-peer sends to the MDB multicast data channel a message whose body is the contents of that chunk
  
 * [ ] Message: **PUTCHUNK \<Version\> \<SenderId\> \<FileId\> \<ChunkNo\> \<ReplicationDeg\> \<CRLF\>\<CRLF\>\<Body\>**
  
 * [ ] A peer that stores the chunk upon receiving the PUTCHUNK message, should reply by sending on the multicast control channel (MC) a confirmation message
    * [ ] **STORED \<Version\> \<SenderId\> \<FileId\> \<ChunkNo\> \<CRLF\>\<CRLF\>**
    * [ ] After a random delay uniformly distributed between 0 and 400 ms
      
 * [ ] A peer must never store the chunks of its own files
  
 * [ ] The initiator-peer collects the confirmation messages during a time interval of one second
    * [ ] If the number of confirmation messages it received up to the end of that interval is lower than the desired replication degree, it retransmits the backup message on the MDB channel, and doubles the time interval for receiving confirmation messages
    * [ ] This procedure is repeated up to maximum number of five times, i.e. the initiator will send at most 5 PUTCHUNK messages per chunk

 * [ ] Because UDP is not reliable, a peer that has stored a chunk must reply with STORED message to every PUTCHUNK message it receives
    * [ ] The initiator-peer needs to keep track of which peers have responded
      
 * [ ] A peer should also count the number of confirmation messages for each of the chunks it has stored and keep that count in non-volatile memory
    * [ ] This information is used if the peer runs out of disk space
       * [ ] The peer will try to free some space by evicting chunks whose actual replication degree is higher than the desired replication degree
  
**[Enhancement]**
  
 * [ ] This scheme can deplete the backup space rather rapidly, and cause too much activity on the nodes once that space is full. Can you think of an alternative scheme that ensures the desired replication degree, avoid these problems, and, nevertheless, can interoperate with peers that execute the chunk backup protocol described above?

### 2. Chunk restore

 * [ ] Uses the same multicast control channel (MC) as the backup protocol, but uses a different multicast channel for data, the multicast data recovery channel (MDR)
  
 * [ ] To recovery a chunk, the initiator-peer shall send a message to the MC
    * [ ] **GETCHUNK \<Version\> \<SenderId\> \<FileId\> \<ChunkNo\> \<CRLF\>\<CRLF\>**
      
 * [ ] Upon receiving this message, a peer that has a copy of the specified chunk shall send it in the body of a CHUNK message via the MDR channel
    * [ ] **CHUNK \<Version\> \<SenderId\> \<FileId\> \<ChunkNo\> \<CRLF\>\<CRLF\>\<Body\>**
      
 * [ ] Each peer shall wait for a random time uniformly distributed between 0 and 400 ms, before sending the CHUNK message
    * [ ] If it receives a CHUNK message before that time expires, it will not send the CHUNK message

**[Enhancement]**

 * [ ] If chunks are larger, this protocol may not be desirable: only one peer needs to receive the chunk, but we are using a multicast channel for that. Can you think of a change to the protocol that would eliminate this problem, and yet interoperate with non-initiator peers that implement the protocol described in this section?

### 3. File deletion

 * [ ] When a file is deleted from its home file system, its chunks should also be deleted from the backup service

 * [ ] The protocol provides the following message, that should be sent on the MC
    * [ ] **DELETE \<Version\> \<SenderId\> \<FileId\> \<CRLF\>\<CRLF\>**
      
 * [ ] Upon receiving this message, a peer should remove from its backing store all chunks belonging to the specified file
  
 * [ ] This message does not elicit any response message
  
 * [ ] An implementation, may send this message as many times as it is deemed necessary to ensure that all space used by chunks of the deleted file are deleted in spite of the loss of some messages
  
**[Enhancement]**

 * [ ] If a peer that backs up some chunk of the file is not running at the time the initiator peer sends a DELETE message for that file, the space used by these chunks will never be reclaimed. Can you think of a change to the protocol, possibly including additional messages, that would allow to reclaim storage space even in that event?

### 4. Space reclaiming

 * [ ] When a peer deletes a copy of a chunk it has backed up, it shall send to the MC channel a message
    * [ ] REMOVED \<Version\> \<SenderId\> \<FileId\> \<ChunkNo\> \<CRLF\>\<CRLF\>
      
 * [ ] Upon receiving this message, a peer that has a local copy of the chunk shall update its local count of this chunk
    * [ ] If this count drops below the desired replication degree of that chunk, it shall initiate the chunk backup subprotocol after a random delay uniformly distributed between 0 and 400 ms
       * [ ] If during this delay, a peer receives a PUTCHUNK message for the same file chunk, it should back off and restrain from starting yet of another backup subprotocol for that file chunk
          
**[Enhancement]**

 * [ ] If the peer that initiates the chunk backup subprotocol fails before finishing it, the replication degree of the file chunk may be lower than that desired. Can you think of a change to the protocol, compatible with the chunk backup subprotocol, i.e. both when a chunk is being backed up for the first time and when a copy of the chunk is deleted.
  
