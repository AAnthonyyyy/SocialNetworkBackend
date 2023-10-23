package com.hgm.data.repository.skill

import com.hgm.data.model.Skill
import org.litote.kmongo.coroutine.CoroutineDatabase

class SkillRepositoryImpl(
    db: CoroutineDatabase
) : SkillRepository {

    val skills = db.getCollection<Skill>()

    override suspend fun getSkills(): List<Skill> {
        return skills.find().toList()
    }
}