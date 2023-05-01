package iskcon.devotees.podcast.data.apiservice

import iskcon.devotees.podcast.data.dto.model.PodcastDto

/** API service interface*/
interface PodcastAPIService {
    fun fetchPodcastList(): List<PodcastDto>
}