package tech.stonks.kvizard.data

import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import tech.stonks.kvizard.data.model.VersionData

interface VersionApi {

    @GET("versions7.json")
    fun getVersionData(): Single<VersionData>

    companion object {
        fun create(): VersionApi {
            return Retrofit.Builder()
                    .baseUrl("https://raw.githubusercontent.com/rjaros/kvision/master/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
                    .create(VersionApi::class.java)
        }
    }
}