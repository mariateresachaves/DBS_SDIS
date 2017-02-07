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

In order to tolerate the unavailability of peers, the service backs up each chunk with a given degree of replication, i.e. on a given number of peers. The desired replication degree of a chunk depends on the file to which it belongs, and all chunks of a given file have the same desired replication degree.
However, at any time instant, the actual replication degree of a chunk may be different from the one that is desired.

In adition to the basic functionality for backing up and recovering a file, the backup service must provide the functionality for reclaiming disk space on peers. **First**, as a requirement of the service, each peer retains total control on the use of its local disk. If a server's administrator decides to reduce the amount of local disk space used by the backup service, it may have to free disk space used for backing up chunks. This will decrease the replication degree of the chunk, which may drop below the desired value. In that case the service will try to create new copies of the chunk so as to keep the desired replication degree. **Second**, a file may be deleted. In this case, the backup service should delete all the chunks of that file. Actually, deletion of the chunks of a file, may happen not only when the file is deleted on its file system, but also when it is modified, because, for the backup system, it will be a different file. **Food for thought**: Is this a problem if we wish to keep the backups of multiple versions of a file?

As described, the backup service knows only about chunks of the backed up files, wich are identified by the field. It knows nothing about the file systems where they are kept. Of course to be of pratical use, the mapping between the fileId kept by the backup system and the name of that file (and possibly its file system) needs to survive a failure of the original file system. This problem can be solved in different ways, but you are not required to do it for this project. For this project, and to keep it feasible for all of you, we will assume that this mapping is never lost.

## Report

https://www.overleaf.com/8064403cwszmfcszkjg
