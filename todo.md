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

  * [ ] \<CRLF\> - '0xD' '0xA'
  
  * [ ] Each header line is a sequence of fields, sequences of ASCII codes separated by spaces, the ASCII char ' '
      * [ ] There may be more than one space between fields
      * [ ] There may be zero or more spaces after the last field in a line
      * [ ] The header always terminates with an empty header line
           * [ ] the \<CRLF\> of the last header line is followed immediatly by another \<CRLF\>, without any character in between

**Body**

  * [ ] ...

## Subprotocols

  * [x] Many of these subprotocols are initiated by a peer, we call that peer the initiator-peer
  
  * [x] The role of initiator-peer can be played by any peer, depending on the particular instance of the subprotocol
  
  * [ ] Multicast Control channel (MC) - used for control messages
      * [ ] All peers must subscribe the MC channel
  
  * [ ] Some subprotocols use also one of two multicast data channels
      * [ ] Multicast Data Backup channel (MDB) - used to backup file chunk data
      * [ ] Multicast Data Restore channel (MDR) - used to restore file chunk data

  * [ ] The IP multicast addresses of these channels should be configurable
      * [ ] 6 command line arguments of the server program
          * [ ] MC, MDB, MDR, with IP multicast address of each channel before the respective port number
          * [ ] These arguments must follow immediately the first command line argument, which is the server id

### 1. Chunk backup

  * [ ] ...

### 2. Chunk restore

  * [ ] ...

### 3. File deletion

  * [ ] ...

### 4. Space reclaiming

  * [ ] ...
