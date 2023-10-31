package com.hgm.data.repository.like

import com.hgm.data.model.Comment
import com.hgm.data.model.Like
import com.hgm.data.model.Post
import com.hgm.data.model.User
import com.hgm.data.utils.ParentType
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setValue

class LikeRepositoryImpl(
    db: CoroutineDatabase
) : LikeRepository {

    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()
    private val posts = db.getCollection<Post>()
    private val comments = db.getCollection<Comment>()

    override suspend fun likePost(userId: String, parentId: String, parentType: Int): Boolean {
        val isUserExist = users.findOneById(userId) != null

        return if (isUserExist) {
            //根据点赞类型更新点赞量
            when (parentType) {
                ParentType.Post.type -> {
                    val post = posts.findOneById(parentId) ?: return false
                    posts.updateOneById(
                        id = parentId,
                        update = setValue(Post::likeCount, post.likeCount + 1)
                    )
                }

                ParentType.Comment.type -> {
                    val comment = comments.findOneById(parentId) ?: return false
                    comments.updateOneById(
                        id = parentId,
                        update = setValue(Comment::likeCount, comment.likeCount + 1)
                    )
                }

                else -> Unit
            }

            likes.insertOne(
                Like(
                    userId = userId,
                    parentId = parentId,
                    parentType = parentType,
                    timestamp = System.currentTimeMillis()
                )
            )
            true
        } else false
    }

    override suspend fun unlikePost(userId: String, parentId: String, parentType: Int): Boolean {
        val isUserExist = users.findOneById(userId) != null
        return if (isUserExist) {
            when (parentType) {
                ParentType.Post.type -> {
                    val post = posts.findOneById(parentId) ?: return false
                    posts.updateOneById(
                        id = parentId,
                        update = setValue(Post::likeCount, (post.likeCount - 1).coerceAtLeast(0))
                    )
                }

                ParentType.Comment.type -> {
                    val comment = comments.findOneById(parentId) ?: return false
                    comments.updateOneById(
                        id = parentId,
                        update = setValue(Comment::likeCount, (comment.likeCount - 1).coerceAtLeast(0))
                    )
                }

                else -> Unit
            }

            likes.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
                )
            )
            true
        } else false
    }

    override suspend fun removeLikeForParent(parentId: String) {
        likes.deleteMany(Like::parentId eq parentId)
    }

    override suspend fun getLikesForParent(parentId: String, page: Int, pageSize: Int): List<Like> {
        return likes.find(Like::parentId eq parentId)
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Like::timestamp)
            .toList()
    }
}