package repositories

import com.datastax.driver.core.Row
import config.CassandraConfig
import javax.inject.Inject
import models.Post
import play.api.Configuration

class PostRepository @Inject()(configuration: Configuration) extends CassandraConfig(configuration){

  lazy val schemaCreatePost =
    """
      |INSERT INTO tutorialspoint.posts(posts_id, posts_content)
      |VALUES(?, ?)
    """.stripMargin
  lazy val createPostStatement = session.prepare(schemaCreatePost)

  def createPost(post: Post) = {
    val statement = createPostStatement.bind()
    statement.setInt("posts_id", post.id)
    statement.setString("posts_content", post.content)

    session.execute(statement)
  }

  lazy val schemaGetPost =
    """
      |SELECT * FROM tutorialspoint.posts
      |WHERE posts_id = ?
      |LIMIT 1
    """.stripMargin
  lazy val getPostStatement = session.prepare(schemaGetPost)

  def getPost(postId: Int): Option[Post] = {
    val row = session.execute(getPostStatement.bind(postId)).one()
    if (row != null) {
      Option(postRowToModel(row))
    } else None
  }

  def postRowToModel(row: Row) = Post(row.getInt("posts_id"), row.getString("posts_content"))
}
