package com.hgm.data.repository.skill

import com.hgm.data.model.Skill

interface SkillRepository {

    suspend fun getSkills():List<Skill>
}