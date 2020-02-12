package controllers

import config.CassandraConfig
import javax.inject.{Inject, Singleton}
import models.{Comment, Post}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import repositories.{CommentRepository, PostRepository}

@Singleton
class ApiController @Inject() (cc: ControllerComponents, postRepository: PostRepository, commentRepository: CommentRepository) extends AbstractController(cc){

  def ping() = Action { implicit request =>
    Ok("Hello, Scala!")
  }

  def initDatabase() = Action { implicit request =>
    val postSeq = Seq(
      Post(1, "This is the first post"),
      Post(2, "Another post"),
      Post(3, "Third post")
    )
    postSeq.map(post => postRepository.createPost(post))

    val commentSeq = Seq(Comment(1, 1, "This is an awesome blog post", "Fantastic Mr Fox"),
      Comment(2, 1, "Thanks for the insights", "Jane Doe"),
      Comment(3, 2, "Great, thanks for this post", "Joe Bloggs")
    )
    commentSeq.map(c => commentRepository.createComment(c))

    Ok("Database Initialised!")
  }

  def getPost(postId: Int) = Action { implicit request =>
    postRepository.getPost(postId).map(post => Ok(Json.toJson(post))).getOrElse(NotFound)
  }

  def getComments(postId: Int) = Action { implicit request =>
    Ok(Json.toJson(commentRepository.getComments(postId)))
  }
}
