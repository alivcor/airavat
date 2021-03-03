from pyspark.sql import SparkSession
from pyspark import SparkConf, SparkContext
import logging

logger = logging.getLogger(__name__)

class SparkController():
    def __init__(self, session):
        self.session = session
        self.sparkConf = SparkConf()
        sparkConf = dict(self.session.conf.items('spark'))
        for configKey in sparkConf:
            if configKey != "master":
                self.sparkConf.set(configKey, sparkConf.get(configKey))
        self.sparkMaster = "local" if "master" not in sparkConf else sparkConf.get("master")
        logger.info("Using Spark Master = " + self.sparkMaster)
        self.spark = SparkSession \
            .builder \
            .master(self.sparkMaster) \
            .appName("AiravatServer") \
            .config(conf=self.sparkConf) \
            .getOrCreate()
        netflix = self.spark.read.option("header", True).csv("/Users/abhinandandubey/Desktop/airavat/data/netflix_titles.csv")
        netflix.show()
        netflix.createOrReplaceTempView("netflix")

    def __deserialize(self, plan):
        plans = plan.split("\n\n")
        print(len(plans))
        print(plans[-1])

    def getCost(self, sqlStatement):
        df = self.spark.sql("EXPLAIN EXTENDED " + sqlStatement)
        row_list = df.select('plan').collect()
        df2 = self.spark.sql("EXPLAIN COST "+ sqlStatement)
        df2.show(truncate=False)
        self.__deserialize(row_list[0].plan)
        return row_list[0].plan