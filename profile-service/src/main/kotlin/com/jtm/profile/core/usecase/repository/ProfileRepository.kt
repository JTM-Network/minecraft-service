package com.jtm.profile.core.usecase.repository

import com.jtm.profile.core.domain.entity.Profile
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProfileRepository: ReactiveMongoRepository<Profile, String>