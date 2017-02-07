# Distributed Backup Service

This project is a distibuted backup service for a local area network (LAN). The objective is to use unused disk space of the computers in a LAN for backing up files in other computers in the same LAN. Te service is provided by servers, one per computer, in an environment that is assumed cooperative (rather than hostile). Nevertheless, each server retains control over own disks and, if needed, may reclaim the space it made available for backing up other computers' files.

## Assumptions

  * The system is composed by a set of computers interconnected by a local area network. The service is provided by a set of servers, **possibly more than one per computer**, that participates in the backup service. A server manages local disk storage where it may store files, or parts thereof, and is identified by an integer, wich is assumed to be unique and that never change.

  * The network may loose or duplicate messages, but that network failures are transient. I.e., if the sender keeps retransmitting a message, it will eventually reach its destination.

  * The loss data by a server is an event that is statistically independent of the loss of data by any other server, whether or not it is on the same computer.
  
  * A local area network administered by a single entity in a friendly environment. All participants behave according to the protocol specified and do not attempt to take advantage of the service or to attack it.
  
  * The participants do not modify either intentionally or accidentally, the backed up data.

## Service

The backup service is provided by a set of servers. Because no server is special, we call these servers "peers". (This kind of implementation is often called serverless service.) As stated earlier, **each peer is identified by an integer**, which is unique among the set of peers in the system.

### Service Description

The purpose of the service is to backup files by replicating their content in multiple servers. We assume that each file has a "home" server, which has the original copy of the file. Although the file will be stored on some file system, which may be distributed, the backup service will generate an identifier for each file backs up. This identifier is obtained by applying SHA256, a cryptographic hash function, to some \*bit string\*. Each implementation can choose the \*bit string\* used to generate a file identifier, as long as that choice generates file identifiers that are unique with very high probability, i.e. that bit string should be unique. Furthermore, because the backup service is not aware of versions of a file, the bit string used to generate a file identifier should include data and or metadata that ensures that a modified file has a different field. As a suggestion you can combine the file metadata (file name, date modified, owner, ...) and/or file data to generate that bit string.

The backup service splits each file in chunks and then backs up each chunk independently, rather than crating multiple files that are a copy of the file to backup. Each chunk is identified by the pair (field, chunkNo). The size of each chunk is 64KByte (where K stands for 1000). The size of the last chunk is always shorter than that size. **If the file size is a multiple of the chunk size, the last chunk has size 0**. A peer need not store all chunks of a file, or even any chunk of a file. The recovery of each chunk is also performed independently of the recovery of other chunks of a file.



## Report

https://www.overleaf.com/8064403cwszmfcszkjg
