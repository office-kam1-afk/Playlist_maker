//ackage com.example.playlistmaker.data.network

//import com.example.playlistmaker.data.network.SearchResponse
//import com.example.playlistmaker.data.dto.SearchResponseDto
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query

//object NetworkClient {
//    private const val BASE_URL = "https://itunes.apple.com/"

  //  private val retrofit = Retrofit.Builder()
    //    .baseUrl(BASE_URL)
      //  .addConverterFactory(GsonConverterFactory.create())
        //.build()

//    val api: iTunesApiService = retrofit.create(iTunesApiService::class.java)
//}

//interface iTunesApiService {
  //  @GET("search")
  //  suspend fun search(@Query("term") query: String, @Query("entity") entity: String = "song"): SearchResponseDto
//}