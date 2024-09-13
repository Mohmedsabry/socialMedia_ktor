package com.example.routing

import com.example.data.dto.*
import com.example.data.mapper.toDto
import com.example.data.mapper.toPost
import com.example.data.mapper.toUser
import com.example.data.mapper.toUserReceive
import com.example.data.repository.AuthRepositoryImp
import com.example.data.repository.FriendsRepositoryImpl
import com.example.data.repository.PostRepositoryImpl
import com.example.data.util.DateBaseError
import com.example.domain.model.Friend
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.FriendsRepository
import com.example.domain.repository.PostRepository
import com.example.domain.util.Result
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async

fun Application.authRoute(authRepository: AuthRepository = AuthRepositoryImp()) {
    routing {
        post("/register") {
            try {
                val userDto = call.receive<RegisterDto>()
                val user = userDto.toUser()
                println(user)
                val result = async {
                    authRepository.signUp(user)
                }.await()
                when (result) {
                    is Result.Failure -> {
                        when (result.error) {
                            DateBaseError.NO_CONNECTION -> {
                                call.respond(HttpStatusCode.InternalServerError, "no db connection")
                            }

                            DateBaseError.CAN_NOT_INSERT -> {
                                call.respond(HttpStatusCode.InternalServerError, "can not insert value")
                            }
                        }
                    }

                    is Result.Success -> {
                        call.respond("register successfully")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, e.message.toString())
            }
        }
        post("/login") {
            val userLogin = call.receive<LoginDto>()
            val res = async { authRepository.login(userLogin.email, userLogin.password) }.await()
            when (res) {
                is Result.Failure -> {
                    call.response.headers.append("error", res.error.massage)
                    call.respond(HttpStatusCode.BadRequest, res.error.massage)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.data.toDto())
                }
            }
        }
        get("/emails") {
            when (val emails = authRepository.getAllEmails()) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.InternalServerError)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, emails.data)
                }
            }
        }
        get("/user") {
            val email = call.request.queryParameters["email"] ?: ""
            when (val res = authRepository.getUserByEmail(email)) {
                is Result.Failure -> {
                    call.response.headers.append("error", res.error.massage)
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.data.toDto())
                }
            }
        }
    }
}

fun Application.postRouting(
    postRepository: PostRepository = PostRepositoryImpl()
) {
    routing {
        get("/posts") {
            // get all posts except user posts
            val email = call.request.queryParameters["email"] ?: ""
            when (val posts = postRepository.getAllPosts(email)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, posts.data.map { it.toUserReceive() })
                }
            }
        }
        get("/profile") {
            // get all posts for me
            val email = call.request.queryParameters["email"] ?: ""
            when (val res = postRepository.getMyPosts(email)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.data.map { it.toUserReceive() })
                }
            }
        }
        get("/post/{id}") {
            // get specific post by id (shared or ordinal)
            val id = call.parameters["id"]?.toInt() ?: 0
            val isShared = call.request.queryParameters["isShared"]?.toBoolean() ?: false
            when (val res = postRepository.getPostById(id, isShared)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.data.toUserReceive())
                }
            }
        }
        post("/post") {
            val post = call.receive<PostDto>()
            when (postRepository.addPost(post.toPost())) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        put("/post/{id}") {
            val content = call.request.queryParameters["content"] ?: ""
            val id = call.parameters["id"]?.toInt() ?: 0
            when (content != "" && id != 0) {
                true -> {
                    when (postRepository.updatePost(id, content)) {
                        is Result.Failure -> {
                            call.respond(HttpStatusCode.BadRequest)
                        }

                        is Result.Success -> call.respond(HttpStatusCode.OK)
                    }
                }

                false -> call.respond(HttpStatusCode.BadRequest, "please not send empty")
            }
        }
        delete("/post/{id}") {
            val id = call.parameters["id"]?.toInt() ?: 0
            when (postRepository.deletePost(id)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        post("/comment/{id}") {
            val id = call.parameters["id"]?.toInt() ?: 0
            val comment = call.request.queryParameters["comment"] ?: ""
            val email = call.request.queryParameters["email"] ?: ""
            val isShared = call.request.queryParameters["isShared"]?.toBoolean() ?: false
            when (id != 0 && comment != "" && email != "") {
                true -> {
                    when (postRepository.addComment(id, comment, isShared, email)) {
                        is Result.Failure -> {
                            call.respond(HttpStatusCode.BadRequest)
                        }

                        is Result.Success -> call.respond(HttpStatusCode.OK)
                    }
                }

                false -> call.respond(HttpStatusCode.BadRequest, "don't send empty")
            }
        }
        get("/comment/{id}") {
            val postId = call.parameters["id"]?.toInt() ?: 0
            if (postId != 0) {
                when (val res = postRepository.getCommentsForPost(postId)) {
                    is Result.Failure -> call.respond(HttpStatusCode.NotFound)
                    is Result.Success -> call.respond(HttpStatusCode.OK, res.data)
                }
            }
            call.respond(HttpStatusCode.BadRequest, "please send id")
        }
        put("/comment/{id}") {
            val id = call.parameters["id"]?.toInt() ?: 0
            val comment = call.request.queryParameters["comment"] ?: ""
            val email = call.request.queryParameters["email"] ?: ""
            when (postRepository.updateComment(id, comment, email)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        delete("/comment/{id}") {
            val id = call.parameters["id"]?.toInt() ?: 0
            val email = call.request.queryParameters["email"] ?: ""
            when (postRepository.deleteComment(id, email)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        post("/share") {
            val share = call.receive<SharePostDto>()
            when (postRepository.sharePost(share)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        put("/share") {
            val share = call.receive<SharePostDto>()
            when (postRepository.updateSharePostContent(share.postId, share.content, share.editor)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        delete("/share") {
            val share = call.receive<SharePostDto>()
            when (postRepository.deleteSharedPost(share.postId, share.editor)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        put("/like/{id}") {
            val id = call.parameters["id"]?.toInt() ?: 0
            val value = call.request.queryParameters["value"]?.toInt() ?: 0
            val email = call.request.queryParameters["email"] ?: ""
            val isShared = call.request.queryParameters["isShared"]?.toBoolean() ?: false
            when (postRepository.updatePostLikes(id, value, email, isShared)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}

fun Application.friendsRout(
    friendsRepository: FriendsRepository = FriendsRepositoryImpl()
) {
    routing {
        get("/friends") {
            val email = call.request.queryParameters["email"] ?: ""
            when (val res = friendsRepository.getFriendsForUser(email)) {
                is Result.Failure -> {
                    call.response.headers.append("error", res.error.massage)
                    call.respond(HttpStatusCode.OK, emptyList<FriendsDto>())
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.data.map { it.toDto() })
                }
            }
        }
        get("/request") {
            val email = call.request.queryParameters["email"] ?: ""
            when (val res = friendsRepository.getRequestsForUser(email)) {
                is Result.Failure -> {
                    call.response.headers.append("error", res.error.massage)
                    call.respond(HttpStatusCode.OK, emptyList<FriendsDto>())
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.data.map { it.toDto() })
                }
            }
        }
        post("/friends") {
            val user1 = call.request.queryParameters["user1"] ?: ""
            val user2 = call.request.queryParameters["user2"] ?: ""
            if (user1 != "" && user2 != "") {
                when (friendsRepository.addFriend(user1, user2)) {
                    is Result.Failure -> call.respond(HttpStatusCode.BadRequest)
                    is Result.Success -> call.respond(HttpStatusCode.OK)
                }
            } else call.respond(HttpStatusCode.NotAcceptable, "please type user1 and user2")
        }
        put("/friends") {
            val user1 = call.request.queryParameters["user1"] ?: ""
            val user2 = call.request.queryParameters["user2"] ?: ""
            val statues = call.request.queryParameters["statues"] ?: ""
            when (user1 != "" && user2 != "" && statues != "") {
                true -> {
                    when (friendsRepository.updateFriendShip(user1, user2, statues)) {
                        is Result.Failure -> call.respond(HttpStatusCode.BadRequest)
                        is Result.Success -> call.respond(HttpStatusCode.OK)
                    }
                }

                false -> call.respond(HttpStatusCode.BadRequest,)
            }
        }
        put("/close") {
            val isClose = call.request.queryParameters["close"]?.toBoolean() ?: false
            val user1 = call.request.queryParameters["user1"] ?: ""
            val user2 = call.request.queryParameters["user2"] ?: ""
            when (isClose && user1 != "" && user2 != "") {
                true -> {
                    when (friendsRepository.addClose(user1, user2)) {
                        is Result.Failure -> call.respond(HttpStatusCode.BadRequest)
                        is Result.Success -> call.respond(HttpStatusCode.OK)
                    }
                }

                false -> {
                    when (friendsRepository.removeClose(user1, user2)) {
                        is Result.Failure -> call.respond(HttpStatusCode.BadRequest)
                        is Result.Success -> call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}