package com.kotlin.android.app.data.entity

/**
 * Created by oseven on 2020-06-18
 */
data class PersonDetail(
    var advertisement: Advertisement,
    var background: Background
) {
    data class Advertisement(
        var count: Int,
        var error: String,
        var success: Boolean,
        var advList: List<Any>
    )

    data class Background(
        var address: String,
        var bigImage: String,
        var birthDay: Int,
        var birthMonth: Int,
        var birthYear: Int,
        var community: Community,
        var content: String,
        var deathDay: Int,
        var deathMonth: Int,
        var deathYear: Int,
        var height: String,
        var hotMovie: HotMovie,
        var image: String,
        var movieCount: Int,
        var nameCn: String,
        var nameEn: String,
        var profession: String,
        var quizGame: QuizGame,
        var rating: String,
        var ratingFinal: String,
        var style: Style,
        var totalNominateAward: Int,
        var totalWinAward: Int,
        var url: String,
        var awards: List<Awards>,
        var expriences: List<Any>,
        var festivals: List<Festivals>,
        var images: List<Images>,
        var otherHonor: List<Any>,
        var relationPersons: List<Any>
    ) {
        class Community
        class HotMovie
        class QuizGame
        class Style
        data class Awards(
            var festivalId: Int,
            var nominateCount: Int,
            var winCount: Int,
            var nominateAwards: List<NominateAwards>,
            var winAwards: List<Any>
        ) {
            data class NominateAwards(
                var awardName: String,
                var festivalEventYear: String,
                var image: String,
                var movieId: Int,
                var movieTitle: String,
                var movieTitleEn: String,
                var movieYear: String,
                var roleName: String,
                var sequenceNumber: Int
            )
        }

        data class Festivals(
            var festivalId: Int,
            var img: String,
            var nameCn: String,
            var nameEn: String,
            var shortName: String
        )

        data class Images(
            var image: String,
            var imageId: Int,
            var type: Int
        )
    }
}