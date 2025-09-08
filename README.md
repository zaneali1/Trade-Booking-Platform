## Overview
A simulation of a CSV trade booking platform which reads order messages to perform order entry, amendment, cancellation, execution and aggregation. 

New orders must be submitted in the CSV format as:

<p align="center">
<i> TradeID,BBGCode,Currency,Side,Price,Volume,Portfolio,Action,Account,Strategy,User,TradeTimeUTC,ValueDate </i> 
</p>

- `TradeID` is a unique key  
- `Side` is set to `B` or `S` (buy-side or sell-side orders)  
- `Price` is numeric  
- `Volume` is a numeric integer  
- `Action` is set to `NEW`, `AMEND`, or `CANCEL`  
- `TradeTimeUTC` is in the format: `yyyy-MM-ddTHH:mm:ss.SSSSSS`

## Features
### Price-Time Matching Engine

- `Order Books:` Implemented using self-balancing binary search trees (BSTs).
- Ordering Rules
  - `Bid book:` Highest prices first  
  - `Ask book:` Lowest prices first  
  - `Timestamp tie-breaker:` Earlier orders have priority for equal prices
- `Limit Price Queues:` Each BST node represents a limit price and contains a doubly-linked list of orders at that price.
- `Performance:` Operations such as insertion and deletion have complexity:  

O(log(L + O))

Where:
- L = number of unique limit prices  
- O = maximum number of orders per limit price  
Compared to O(log N) for a flat BST over all orders.

### Self-Balancing Trees

- Market activity can produce tall, unbalanced trees, especially when new prices appear at the edges of the book.
- Orders with earlier timestamps but the same price are matched first.
- Without balancing, this can lead to worse amortized runtime for insertions and deletions.
- Self-balancing trees are used to maintain efficient performance and ensure operations remain near `O(log(L + O))`.


### Aggregation
The trading platform supports two approaches for aggregating orders:

1. **By Strategy, Portfolio, and User**  
   - Aggregation is performed on a master order book containing all orders.  
   - Orders are grouped by price and the chosen property (Strategy, Portfolio, or User).  
   - The resulting volumes are summed per group.

2. **By Bloomberg Code**  
   - Each instrument (BBGCode) has its own order book.  
   - The volume for each limit price is aggregated per order per BBGCode.
