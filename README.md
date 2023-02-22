# Trade Booking System
A trade booking platform which reads order messages to simulate order entry, amendment and cancellation operations, and performs executions of orders where necessary.

## Overview
The trade booking system provides the following functionality:
- **Order Entry:** Orders marked *NEW* are received by the platform and order books are updated accordingly.

- **Order Cancellation:** Orders marked *CANCEL* are removed by the platform and order books are updated accordingly (given the target order exists and has not been executed against).

- **Order Amendment:** Orders marked *AMEND* are removed and replaced by the platform with the amended details (given the target order exists and has not been executed against).

- **Order Execution:** The platform automatically executes trades against opposite-limit orders with the the closest possible price to the new order until the volume of the new order is filled (or the opposite-limit order book is empty). Both partial and total executions are possible.

- **Order Aggregation:** The trading platform can aggregate volume by both Price and a chosen property (such as BBGCode Bloomberg Code, Strategy, Portfolio and User).

To interface with the system, a simple, interactive command-line reader tool was created in /src/main/Java/com/app/Main.java. No data validation is provided -- new orders must be submitted in the CSV format as:

*TradeID,BBGCode,Currency,Side,Price,Volume,Portfolio,Action,Account,Strategy,User,TradeTimeUTC,ValueDate*

## Design
## Price-Time Matching Engine
The bid and ask order books were modelled using self-balancing Binary Search Trees. The order books were first arranged by price; high prices took priority in the bid order book while low prices took priority in the ask order book. For equivalent prices, the orders were always prioritised by earliest timestamp regardless of book. As a result of the ordering, an executing order would traverse the fewest possible nodes in the opposite book before terminating. 

To improve the matching performance, each node of the Tree could contain a doubly-linked list with orders of a common limit price, i.e. a Tree<Queue<Order>> data struture. As a result, each node of the tree would represent a limit price instead of an order. Orders within each queue would be prioritised by earliest timestamp. 
  
As a result, the Trading Platform's performance for operations such as insertion and deletion would be improved. Instead O(logN), where N is the number of orders, these procedures would have a complexity of O(log(L + O)) where L is the number of unique limit prices and O is the maximum number of orders per singular limit price. 



## Aggregation
