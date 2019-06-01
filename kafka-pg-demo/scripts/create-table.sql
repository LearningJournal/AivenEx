CREATE TABLE stock_data(
      symbol VARCHAR(100),
      series VARCHAR(100),
      open NUMERIC(8,2),
      high NUMERIC(8,2),
      low NUMERIC(8,2),
      close NUMERIC(8,2),
      last NUMERIC(8,2),
      previousclose NUMERIC(8,2),
      totalTradedQty INTEGER,
      totalTradedVal NUMERIC(16,2),
      tradeDate DATE,
      totalTrades INTEGER,
      isinCode VARCHAR(100)
  );