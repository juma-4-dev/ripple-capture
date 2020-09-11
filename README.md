# ripple-capture

## Build

    $ mvn clean install -U

## Run

### Prerequisites

 * Java JDK > 8
 * Gnuplot (http://www.gnuplot.info/)

### Command

    $ java -jar ./target/ripple-capture.jar %SERVER% %PATH% %INTERVAL% %COUNT%

### Parameters

* SERVER Targeted ripple server
* PATH Destination path for the capture files (e.g. /tmp)
* INTERVAL Polling interval in second
* COUNT Polling count

### Output sample

```
[INFO] Starting capture
- Server: *******
- Id: capture-1599817170059
- Path: /tmp/capture-1599817170059
- Count: 10
- Interval: 1(s)
[INFO] Requesting server info (1/10) ...
- Time (s): 0
- Increment: 0
[INFO] Requesting server info (2/10) ...
- Time (s): 1
- Increment: 1
[INFO] Requesting server info (3/10) ...
- Time (s): 2
- Increment: 1
[INFO] Requesting server info (4/10) ...
- Time (s): 3
- Increment: 1
[INFO] Requesting server info (5/10) ...
- Time (s): 4
- Increment: 1
[INFO] Requesting server info (6/10) ...
- Time (s): 6
- Increment: 2
[INFO] Requesting server info (7/10) ...
- Time (s): 7
- Increment: 2
[INFO] Requesting server info (8/10) ...
- Time (s): 8
- Increment: 2
[INFO] Requesting server info (9/10) ...
- Time (s): 9
- Increment: 3
[INFO] Requesting server info (10/10) ...
- Time (s): 10
- Increment: 3
[INFO] Requesting ledger details (Index=58112086) ...
- Closed: true
- Duration (s): 1
[INFO] Requesting ledger details (Index=58112087) ...
- Closed: true
- Duration (s): 8
[INFO] Requesting ledger details (Index=58112088) ...
- Closed: true
- Duration (s): 1
[INFO] Requesting ledger details (Index=58112089) ...
- Closed: true
- Duration (s): 9
[INFO] Capture terminated
- Path: /tmp/capture-1599817170059
- Min time (s): 1
- Max time (s): 9
- Average time (s): 4.75
```
