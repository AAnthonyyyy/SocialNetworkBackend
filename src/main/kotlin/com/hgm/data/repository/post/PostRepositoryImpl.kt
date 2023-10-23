package com.hgm.data.repository.post

import com.hgm.data.model.Following
import com.hgm.data.model.Post
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class PostRepositoryImpl(
    db: CoroutineDatabase
) : PostRepository {

    private val posts = db.getCollection<Post>()
    private val following = db.getCollection<Following>()

    override suspend fun createPost(post: Post): Boolean {
        return posts.insertOne(post).wasAcknowledged()
    }

    override suspend fun deletePost(postId: String) {
        posts.deleteOneById(postId)
    }

    override suspend fun getPostsByFollows(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Post> {
        //获取我关注列表中所有人的ID
        val userIdFromFollows = following.find(
            Following::followingUserId eq userId
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
    }

    override suspend fun getPostsForProfile(userId: String, page: Int, pageSize: Int): List<Post> {
        //查询个人的所有帖子，降序显示
        return posts.find(Post::userId eq userId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Post::timestamp)
            .toList()
    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)
    }
}