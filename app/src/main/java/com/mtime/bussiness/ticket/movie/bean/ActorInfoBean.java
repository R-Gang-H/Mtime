package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.QuizGameBean;

import java.util.List;

public class ActorInfoBean extends MBaseBean {
    // name birthday, sex, address, 
    private String nameCn;
    private String nameEn;
    private int birthYear;
    private int birthMonth;
    private int birthDay;
    private int deathYear;
    private int deathMonth;
    private int deathDay;
    private String ratingFinal;
    private String address;
    private String profession;
    private String rating;
    private String content;
    private String image;
    private String url;
    private int totalWinAward;
    private int totalNominateAward;

    private List<ActorExperience> expriences;
    private List<ActorRelationShips> relationPersons;
    private List<ActorFestivals> festivals;
    private List<ActorAwards> awards;
    private ActorStyle style;
    private ActorHotMovie hotMovie;
    private int movieCount;

    private List<ActorImage> images;
    private QuizGameBean quizGame;
//    private List<ActorHonorItem> otherHonor;

    // 暂时还么有UI，所以注释掉
//    private CommunityBean community;
//    public CommunityBean getCommunity() {
//        return community;
//    }
//
//    public void setCommunity(CommunityBean community) {
//        this.community = community;
//    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(final String nameCn) {
        this.nameCn = nameCn;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(final String nameEn) {
        this.nameEn = nameEn;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(final int birthYear) {
        this.birthYear = birthYear;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(final int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getDeathDay() {
        return deathDay;
    }

    public void setDeathDay(final int deathDay) {
        this.deathDay = birthDay;
    }

    public int getDeathMonth() {
        return deathMonth;
    }

    public void setDeathMonth(final int deathMonth) {
        this.deathMonth = deathMonth;
    }

    public int getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(final int deathYear) {
        this.deathYear = deathYear;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(final int birthDay) {
        this.birthDay = birthDay;
    }

    public String getRatingFinal() {
        return ratingFinal;
    }

    public void setRatingFinal(final String ratingFinal) {
        this.ratingFinal = ratingFinal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(final String profession) {
        this.profession = profession;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(final String rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public List<ActorImage> getImages() {
        return images;
    }

    public void setImages(final List<ActorImage> images) {
        this.images = images;
    }

    public List<ActorExperience> getExpriences() {
        return expriences;
    }

    public void setExpriences(List<ActorExperience> expriences) {
        this.expriences = expriences;
    }

    public List<ActorRelationShips> getRelationPersons() {
        return relationPersons;
    }

    public void setRelationPersons(List<ActorRelationShips> relationPersons) {
        this.relationPersons = relationPersons;
    }

    public List<ActorFestivals> getFestivals() {
        return festivals;
    }

    public void setFestivals(List<ActorFestivals> festivals) {
        this.festivals = festivals;
    }

    public List<ActorAwards> getAwards() {
        return awards;
    }

    public void setAwards(List<ActorAwards> awards) {
        this.awards = awards;
    }

    public int getTotalWinAward() {
        return totalWinAward;
    }

    public void setTotalWinAward(int totalWinAward) {
        this.totalWinAward = totalWinAward;
    }

    public int getTotalNominateAward() {
        return totalNominateAward;
    }

    public void setTotalNominateAward(int totalNominateAward) {
        this.totalNominateAward = totalNominateAward;
    }

    public ActorHotMovie getHotMovie() {
        return hotMovie;
    }

    public void setHotMovie(ActorHotMovie hotMovie) {
        this.hotMovie = hotMovie;
    }

    public ActorStyle getStyle() {
        return style;
    }

    public void setStyle(ActorStyle style) {
        this.style = style;
    }

    public void setQuizGame(QuizGameBean bean) {
        this.quizGame = bean;
    }

    public QuizGameBean getQuizGame() {
        return quizGame;
    }

//    public List<ActorHonorItem> getOtherHonor() {
//        return otherHonor;
//    }
//
//    public void setOtherHonor(List<ActorHonorItem> otherHonor) {
//        this.otherHonor = otherHonor;
//    }

    public int getMovieCount() {
        return movieCount;
    }

    public void setMovieCount(final int movieCount) {
        this.movieCount = movieCount;
    }

}
