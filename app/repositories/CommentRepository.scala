package repositories

import com.datastax.driver.core.Row
import config.CassandraConfig
import javax.inject.Inject
import models.Comment
import play.api.Configuration

import scala.collection.JavaConverters.asScala

class CommentRepository @Inject()(configuration: Configuration) extends CassandraConfig(configuration){

  lazy val schemaCreateComment =
    """
      |INSERT INTO tutorialspoint.comments(comments_id, comments_post_id, comments_content, comments_author)
      |VALUES(?, ?, ?, ?)
    """.stripMargin
  lazy val createCommentStatement = session.prepare(schemaCreateComment)

  def createComment(comment: Comment) = {
    val statement = createCommentStatement.bind()
    statement.setInt("comments_id", comment.id)
    statement.setInt("comments_post_id", comment.postId)
    statement.setString("comments_content", comment.text)
    statement.setString("comments_author", comment.authorName)

    session.execute(statement)
  }

  lazy val schemaGetComments =
    """
      |SELECT * FROM tutorialspoint.comments
      |WHERE comments_post_id = ?
    """.stripMargin
  lazy val getCommentsStatement = session.prepare(schemaGetComments)

  def getComments(postId: Int): List[Comment] = {
    val rows = session.execute(getCommentsStatement.bind(postId)).iterator
    asScala(rows).toList.map(row => commentRowToModel(row))
  }

  def commentRowToModel(row: Row) = Comment(row.getInt("comments_id"), row.getInt("comments_post_id"), row.getString("comments_content"), row.getString("comments_author"))

}
