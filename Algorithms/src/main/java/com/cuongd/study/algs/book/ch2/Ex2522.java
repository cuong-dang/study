package com.cuongd.study.algs.book.ch2;

public class Ex2522 {
    private final MaxPQ<BuyOrder> buyOrders;
    private final MinPQ<SellOrder> sellOrders;

    public Ex2522(Order ...orders) {
        buyOrders = new MaxPQ<>();
        sellOrders = new MinPQ<>();

        for (Order order : orders)
            if (order instanceof BuyOrder)
                buyOrders.insert((BuyOrder) order);
            else
                sellOrders.insert((SellOrder) order);
    }

    public void runSimulation() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            BuyOrder buyOrder = buyOrders.peek();
            SellOrder sellOrder = sellOrders.peek();
            if (buyOrder.maxPrice >= sellOrder.minPrice) {
                buyOrders.delMax();
                sellOrders.delMax();
                int fairPrice = (buyOrder.maxPrice + sellOrder.minPrice) / 2;
                int remainingSharesToBuy = buyOrder.numShares - sellOrder.numShares;
                System.out.printf("Matched! B: %s S: %s P: %d\n", buyOrder, sellOrder, fairPrice);
                if (remainingSharesToBuy > 0) {
                    buyOrders.insert(new BuyOrder(remainingSharesToBuy, buyOrder.maxPrice));
                } else if (remainingSharesToBuy < 0) {
                    sellOrders.insert(new SellOrder(-remainingSharesToBuy, sellOrder.minPrice));
                }
            } else {
                System.out.println("No more matched orders.");
                return;
            }
        }
        System.out.println("No more buy or sell orders.");
    }

    public static void main(String[] args) {
        System.out.println("Simulation 1");
        Ex2522 sim1 = new Ex2522(new BuyOrder(5, 4), new SellOrder(5, 2));
        sim1.runSimulation();

        System.out.println("Simulation 2");
        Ex2522 sim2 = new Ex2522(new BuyOrder(5, 4), new SellOrder(2, 2), new SellOrder(3, 2));
        sim2.runSimulation();

        System.out.println("Simulation 3");
        Ex2522 sim3 = new Ex2522(new BuyOrder(5, 4), new SellOrder(5, 5));
        sim3.runSimulation();

        System.out.println("Simulation 4");
        Ex2522 sim4 = new Ex2522(new BuyOrder(5, 4), new SellOrder(5, 5), new SellOrder(5, 4));
        sim4.runSimulation();

        System.out.println("Simulation 5");
        Ex2522 sim5 = new Ex2522(new BuyOrder(5, 4), new BuyOrder(5, 5), new SellOrder(10, 4));
        sim5.runSimulation();
    }

    private static class Order {
        public final int numShares;

        public Order(int numShares) {
            this.numShares = numShares;
        }
    }

    private static class BuyOrder extends Order implements Comparable<BuyOrder> {
        public final int maxPrice;

        public BuyOrder(int numShares, int maxPrice) {
            super(numShares);
            this.maxPrice = maxPrice;
        }

        @Override
        public int compareTo(BuyOrder o) {
            if (this.maxPrice != o.maxPrice)
                return this.maxPrice - o.maxPrice;
            return this.numShares - o.numShares;
        }

        @Override
        public String toString() {
            return String.format("B%d@%d", numShares, maxPrice);
        }
    }

    private static class SellOrder extends Order implements Comparable<SellOrder> {
        public final int minPrice;

        public SellOrder(int numShares, int minPrice) {
            super(numShares);
            this.minPrice = minPrice;
        }

        @Override
        public int compareTo(SellOrder o) {
            if (this.minPrice != o.minPrice)
                return this.minPrice - o.minPrice;
            return this.numShares - o.numShares;
        }

        @Override
        public String toString() {
            return String.format("B%d@%d", numShares, minPrice);
        }
    }
}
