from pyspark.sql import SparkSession
from pyspark import SparkConf, SparkContext

class SparkController():
    def __init__(self, session):
        self.session = session
        self.sparkConf = SparkConf()
        for sparkConfig in self.session.conf.get("spark"):
            self.sparkConf.set(sparkConfig, self.session.conf.get("spark")(sparkConfig))
        self.spark = SparkSession \
            .builder \
            .config(conf=self.sparkConf) \
            .getOrCreate()

    def getCost(self, sqlStatement):
        return self.spark.sql(sqlStatement).explain()