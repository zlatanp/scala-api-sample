package config

import com.datastax.driver.core._
import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class CassandraConfig @Inject()(config: Configuration) {

  lazy val cassandraKeyspace = config.get[String]("cassandra.keyspace")
  lazy val cassandraHosts = config.get[String]("cassandra.hosts").split(",").toSeq.map(_.trim)
  lazy val cassandraPort = config.get[Int]("cassandra.port")

  lazy val poolingOptions: PoolingOptions = {
    new PoolingOptions()
      .setConnectionsPerHost(HostDistance.LOCAL, 4, 10)
      .setConnectionsPerHost(HostDistance.REMOTE, 2, 4)
  }

  lazy val cluster: Cluster = {
    val builder = Cluster.builder()
    for (cp <- cassandraHosts) builder.addContactPoint(cp)
    builder.withPort(cassandraPort)
    builder.withPoolingOptions(poolingOptions)

    builder.build()
  }

  lazy implicit val session: Session = cluster.connect()
}
