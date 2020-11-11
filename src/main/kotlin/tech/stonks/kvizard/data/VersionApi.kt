package tech.stonks.kvizard.data

import io.reactivex.rxjava3.core.Single
import tech.stonks.kvizard.data.model.VersionData

interface VersionApi {
    fun getUpdateData(): Single<VersionData>
}