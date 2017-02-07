# Distributed Backup Service

This project is a distibuted backup service for a local area network (LAN). The objective is to use unused disk space of the computers in a LAN for backing up files in other computers in the same LAN. Te service is provided by servers, one per computer, in an environment that is assumed cooperative (rather than hostile). Nevertheless, each server retains control over own disks and, if needed, may reclaim the space it made available for backing up other computers' files.

## 1. Assumptions

  * The system is composed by a set of computers interconnected by a local area network. The service is provided by a set of servers, **possibly more than one per computer**, that participates in the backup service. A server manages local disk storage where it may store files, or parts thereof, and is identified by an integer, wich is assumed to be unique and that never change.

  * The network may loose or duplicate messages, but that network failures are transient. I.e., if the sender keeps retransmitting a message, it will eventually reach its destination.

  * The loss data by a server is an event that is statistically independent of the loss of data by any other server, whether or not it is on the same computer.
  
  * A local area network administered by a single entity in a friendly environment. All participants behave according to the protocol specified and do not attempt to take advantage of the service or to attack it.
  
  * The participants do not modify either intentionally or accidentally, the backed up data.

## 2. Service

The backup service is provided by a set of servers. Because no server is special, we call these servers "peers". (This kind of implementation is often called serverless service.) As stated earlier, **each peer is identified by an integer**, which is unique among the set of peers in the system.

### 2.1. Service Description

The purpose of the service is to backup files by replicating their content in multiple servers. We assume that each file has a "home" server, which has the original copy of the file. Although the file will be stored on some file system, which may be distributed, the backup service will generate an identifier for each file backs up. This identifier is obtained by applying SHA256, a cryptographic hash function, to some \*bit string\*. Each implementation can choose the \*bit string\* used to generate a file identifier, as long as that choice generates file identifiers that are unique with very high probability, i.e. that bit string should be unique. Furthermore, because the backup service is not aware of versions of a file, the bit string used to generate a file identifier should include data and or metadata that ensures that a modified file has a different field. As a suggestion you can combine the file metadata (file name, date modified, owner, ...) and/or file data to generate that bit string.

The backup service splits each file in chunks and then backs up each chunk independently, rather than crating multiple files that are a copy of the file to backup. Each chunk is identified by the pair (field, chunkNo). The size of each chunk is 64KByte (where K stands for 1000). The size of the last chunk is always shorter than that size. **If the file size is a multiple of the chunk size, the last chunk has size 0**. A peer need not store all chunks of a file, or even any chunk of a file. The recovery of each chunk is also performed independently of the recovery of other chunks of a file.

In order to tolerate the unavailability of peers, the service backs up each chunk with a given degree of replication, i.e. on a given number of peers. The desired replication degree of a chunk depends on the file to which it belongs, and all chunks of a given file have the same desired replication degree.
However, at any time instant, the actual replication degree of a chunk may be different from the one that is desired.

In adition to the basic functionality for backing up and recovering a file, the backup service must provide the functionality for reclaiming disk space on peers. **First**, as a requirement of the service, each peer retains total control on the use of its local disk. If a server's administrator decides to reduce the amount of local disk space used by the backup service, it may have to free disk space used for backing up chunks. This will decrease the replication degree of the chunk, which may drop below the desired value. In that case the service will try to create new copies of the chunk so as to keep the desired replication degree. **Second**, a file may be deleted. In this case, the backup service should delete all the chunks of that file. Actually, deletion of the chunks of a file, may happen not only when the file is deleted on its file system, but also when it is modified, because, for the backup system, it will be a different file. **Food for thought**: Is this a problem if we wish to keep the backups of multiple versions of a file?

As described, the backup service knows only about chunks of the backed up files, wich are identified by the field. It knows nothing about the file systems where they are kept. Of course to be of pratical use, the mapping between the fileId kept by the backup system and the name of that file (and possibly its file system) needs to survive a failure of the original file system. This problem can be solved in different ways, but you are not required to do it for this project. For this project, and to keep it feasible for all of you, we will assume that this mapping is never lost.

### 2.2. Service Interface

Will be based on a command line interface (CLI) application, which will then have to communicate with the local servers. Essentially, it will provide the functionality required to test the protocol.

## 3. Protocol

The protocol used by the backup service comprises several smaller subprotocols, which are used for specific tasks, and that can be run concurrently:

 1. chunk backup
 2. chunk restore
 3. file deletion
 4. space reclaiming
 
Many of these subprotocols are initiated by a peer. To distinguish it from the other peers, in the description of these protocols, we call that peer the **initiator-peer**. The other peers are called peers only. The role of initiator-peer can be played by any peer, depending on the particular instance of the subprotocol.

All subprotocols use a **multicast channel, the control channel (MC)**, that is used for control messages. All peers must subscribe the MC channel. Some subprotocols use also one of two multicast data channels, **MDB** and **MDR**, which are used to **backup** and **restore** file chunk data respectively.

**Note** The IP multicast addresses of these channels should be configurable via 6 command line arguments of the server program, in the following order MC, MDB, MDR, with IP multicast address of each channel before the respective port number. These arguments must follow immediately the first command line argument, which is the server id.

### 3.1. Message Format and Field Encoding

The generic message is composed by two parts: a header and the body. The header contains essentially control information, whereas the body is used for the data and is used in only some messages.

**Header**

The header consists of a sequence of ASCII codes **terminated with the sequence** '0xD' '0xA', which we denote \<CRLF\> because these are the ASCII codes of CR and LF chars respectively. Each header line is a sequence of fields, sequences of ASCII codes separated by spaces, the ASCII char ' '. **Note that:**

 1. there may be more than one space between fields;
 2. there may be zero or more spaces after the last field in a line;
 3. the header always terminates with an empty header line. I.e. the \<CRLF\> of the last header line is followed **immediatly by another \<CRLF\>, without any character in between.**
 
In the version described herein, the header has only the following non-empty single line:

**\<MessageType\> \<Version\> \<SenderId\> \<FileId\> \<ChunkNo\> \<ReplicationDeg\> \<CRLF\>**

Some of these fields may not be used by some messages, but all fields that appear in a message must appear in the relative order specified above.

**\<MessageType\>**

This is type of the message. Each subprotocol specifies its own message types. This fields determines the format of the message and what actions its receivers should perform. This is encoded as a variable length sequance of ASCII characters.

**\<Version\>**

This is the version of the protocol. It is a three ASCII char sequence with the format \<n\>'.'\<m\>, where \<n\> and \<m\> are the ASCII codes of digits. For example, version 1.0, the one specified in this document, should be encoded as the char sequence '1''.''0'.

**\<SenderId\>**

This is the id of the server that has sent the message. This field is useful in many subprotocols. This is encoded as a variable length sequence of ASCII digits.

**\<FileId\>**

This is the file identifier for the backup service. As stated above, it is supposed to be obtained by using the SHA256 cryptographic hash function. As its name indicates its lenght is 256 bit, i.e. 32 bytes, and should be encoded as a 64 ASCII caracter sequence. The encoding is as follow: each byte of the hash value is encoded by the two ASCII characters corresponding to the hexadeciaml representation of that byte. E.g., a byte with value 0xB2 should be represented by the two char sequence 'B''2' (or 'b''2', it does not matter). The entire hash is represented in big-endian order, i.e. from the MSB (byte 31) to LSB (byte 0).

**\<ChunkNo\>**

This field together with the FileId specifies a chunk in the file. The chunk numbers are integers and should be assigned sequentially starting at 0. It is encoded as a sequence of ASCII characters corresponding to the decimal representation of that number, with the most significant digit first. The length of this field is variable, but should not be larger than 6 chars. Therefore, each file can have at most one million chunks. Given that each chunk is 64kByte, this limits the size of the files to backup to 64GByte.

**\<ReplicationDeg\>**

This field contains the desired replication degree of the chunk. This is a digit, thus allowing a replication degree of up to 9. It takes one byte, which is the ASCII code of that digit.

**Body**

When present, the body contains the data of a file chunk. The length of the body is variable. As stated above, if it is smaller than the maximum chunk size, 64 KByte, it is the last chunk in a file. The protocol does not interpret the contents of the Body. For the protocol its value is just a byte sequence.

## Report

https://www.overleaf.com/8064403cwszmfcszkjg
