package com.hgm.service

import com.hgm.data.model.Skill
import com.hgm.data.repository.skill.SkillRepository

class SkillService(
    private val repository: SkillRepository
) {

    suspend fun getSkills(): List<Skill> {
        return repository.getSkills()
    }
}