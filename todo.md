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

## Subprotocols

### 1. Chunk backup

  * [ ] ...

### 2. Chunk restore

  * [ ] ...

### 3. File deletion

  * [ ] ...

### 4. Space reclaiming

  * [ ] ...
