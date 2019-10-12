# Результаты профилирования 

## GET
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
## PUT
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
## DELETE
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
