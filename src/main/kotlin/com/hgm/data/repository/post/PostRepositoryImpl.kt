package com.hgm.data.repository.post

import com.hgm.data.model.Following
import com.hgm.data.model.Like
import com.hgm.data.model.Post
import com.hgm.data.model.User
import com.hgm.data.responses.PostResponse
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.inc

class PostRepositoryImpl(
    db: CoroutineDatabase
) : PostRepository {

    private val posts = db.getCollection<Post>()
    private val users = db.getCollection<User>()
    private val likes = db.getCollection<Like>()
    private val followings = db.getCollection<Following>()

    override suspend fun createPost(post: Post): Boolean {
        return posts.insertOne(post).wasAcknowledged().also { wasAcknowledged ->
            if (wasAcknowledged) {
                users.updateOneById(
                    post.userId,
                    inc(User::postCount, 1)
                )
            }
        }
    }

    override suspend fun deletePost(postId: String) {
        posts.findOneById(postId)?.also { post ->
            users.updateOneById(
                post.userId,
                inc(User::postCount, -1)
            )
        }
        posts.deleteOneById(postId)
    }

    override suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int,
        pageSize: Int
    ): List<PostResponse> {
        //获取我关注列表中所有人的ID
        val userIdFromFollows = followings.find(
            Following::followingUserId eq ownUserId
        )
            .toList()
            .map {
                it.followedUserId
            }


        //通过查询属于这个ID的所有帖子，并按时间降序显示
        return posts.find(Post::userId `in` userIdFromFollows)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
            .map { post ->
                val isLiked = likes.findOne(
                    and(
                        Like::parentId eq post.id,
                        Like::userId eq ownUserId
                    )
                ) != null

                val user = users.findOneById(post.userId)

                PostResponse(
                    id = post.id,
                    userId = post.userId,
                    username = user?.username ?: "",
                    imageUrl = post.imageUrl,
                    profilePictureUrl = user?.profileImageUrl ?: "",
                    description = post.description,
                    likeCount = post.likeCount,
                    commentCount = post.commentCount,
                    isLiked = isLiked,
                    isOwnPost = ownUserId == post.userId
                )
            }
    }

    override suspend fun getPostsForProfile(
        ownUserId: String,
        userId: String,
        page: Int,
        pageSize: Int
    ): List<PostResponse> {
        val user = users.findOneById(userId) ?: return emptyList()
        //查询个人的所有帖子，降序显示
        return posts.find(Post::userId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
            .map { post ->
                //查询我是否有点赞
                val isLiked = likes.findOne(
                    and(
                        Like::userId eq ownUserId,
                        Like::parentId eq post.id
                    )
                ) != null

                PostResponse(
                    id = post.id,
                    userId = post.userId,
                    username = user.username,
                    imageUrl = post.imageUrl,
                    profilePictureUrl = post.imageUrl,
                    description = post.description,
                    likeCount = post.likeCount,
                    commentCount = post.commentCount,
                    isLiked = isLiked,
                    isOwnPost = userId == post.userId
                )
            }
    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)
    }

    override suspend fun getPostDetails(ownUserId: String, postId: String): PostResponse? {
        val post = posts.findOneById(postId) ?: return null
        val user = users.findOneById(post.userId) ?: return null
        val isLiked = likes.findOne(
            and(
                Like::userId eq ownUserId,
                Like::parentId eq post.id
            )
        ) != null

        return PostResponse(
            id = post.id,
            userId = post.userId,
            username = user.username,
            imageUrl = post.imageUrl,
            profilePictureUrl = post.imageUrl,
            description = post.description,
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            isLiked = isLiked,
            isOwnPost = ownUserId == post.userId
        )
    }
}