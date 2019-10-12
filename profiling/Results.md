# Результаты профилирования 

## GET
### 1
```
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ wrk -t4 -c4 -d30s -s ./wrk/get.lua --latency http://localhost:8080
Running 30s test @ http://localhost:8080
  4 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   251.04us    1.41ms  46.71ms   98.31%
    Req/Sec     7.77k     2.04k   11.27k    67.33%
  Latency Distribution
     50%   91.00us
     75%  120.00us
     90%  210.00us
     99%    3.04ms
  927517 requests in 30.01s, 61.03MB read
  Non-2xx or 3xx responses: 927517
Requests/sec:  30903.28
Transfer/sec:      2.03MB
```
### 2
```
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ wrk -t3 -c3 -d60s -s ./wrk/get.lua --latency http://localhost:8080
Running 1m test @ http://localhost:8080
  3 threads and 3 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   115.88us  546.33us  36.11ms   99.12%
    Req/Sec     9.57k     1.78k   11.03k    85.57%
  Latency Distribution
     50%   78.00us
     75%   92.00us
     90%  122.00us
     99%  581.00us
  1715714 requests in 1.00m, 112.90MB read
  Non-2xx or 3xx responses: 1715714
Requests/sec:  28547.20
Transfer/sec:      1.88MB
```

## PUT

### 1
```
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ wrk -t3 -c3 -d30s -s ./wrk/put.lua --latency http://localhost:8080
Running 30s test @ http://localhost:8080
  3 threads and 3 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   229.40us    1.28ms  29.33ms   98.97%
    Req/Sec     7.36k     0.97k   11.94k    76.47%
  Latency Distribution
     50%  111.00us
     75%  129.00us
     90%  158.00us
     99%    1.69ms
  660211 requests in 30.10s, 42.18MB read
Requests/sec:  21933.51
Transfer/sec:      1.40MB
```

### 2
```
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ wrk -t6 -c6 -d30s -s ./wrk/put.lua --latency http://localhost:8080
Running 30s test @ http://localhost:8080
  6 threads and 6 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     0.89ms    3.55ms  96.82ms   95.59%
    Req/Sec     3.68k     1.35k    6.51k    67.83%
  Latency Distribution
     50%  213.00us
     75%  305.00us
     90%  709.00us
     99%   16.80ms
  660021 requests in 30.02s, 42.17MB read
Requests/sec:  21985.48
Transfer/sec:      1.40MB
```

## DELETE
### 1
```
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ wrk -t3 -c3 -d60s -s ./wrk/delete.lua --latency http://localhost:8080
Running 1m test @ http://localhost:8080
  3 threads and 3 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   437.09us    3.10ms 122.47ms   98.01%
    Req/Sec     6.93k     1.67k    8.90k    78.67%
  Latency Distribution
     50%  107.00us
     75%  133.00us
     90%  201.00us
     99%    8.36ms
  1241057 requests in 1.00m, 80.48MB read
Requests/sec:  20669.65
Transfer/sec:      1.34MB
```
### 2
```
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ wrk -t5 -c5 -d30s -s ./wrk/delete.lua --latency http://localhost:8080
Running 30s test @ http://localhost:8080
  5 threads and 5 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   238.29us  836.46us  30.79ms   99.17%
    Req/Sec     5.11k   648.49     6.53k    76.01%
  Latency Distribution
     50%  163.00us
     75%  202.00us
     90%  262.00us
     99%    0.86ms
  764851 requests in 30.10s, 49.60MB read
Requests/sec:  25410.11
Transfer/sec:      1.65MB
```


## STOP
```
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ wrk -t4 -c4 -d60s -s ./wrk/stop.lua --latency http://localhost:8080
Running 1m test @ http://localhost:8080
  4 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   250.93us    1.23ms  13.70ms   98.25%
    Req/Sec     0.00      0.00     0.00       nan%
  Latency Distribution
     50%  105.00us
     75%  126.00us
     90%  155.00us
     99%   11.34ms
  400 requests in 1.00m, 27.73KB read
  Non-2xx or 3xx responses: 400
Requests/sec:      6.67
Transfer/sec:     473.29B
(base) MacBook-Air-Maria:2019-highload-dht stakenschneidermac$ 

```
