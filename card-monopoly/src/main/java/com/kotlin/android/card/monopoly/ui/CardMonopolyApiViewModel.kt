package com.kotlin.android.card.monopoly.ui

import androidx.annotation.IntRange
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.card.monopoly.repository.CardMonopolyApiRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.monopoly.*

/**
 *
 * Created on 2020/6/18.
 *
 * @author o.s
 */
class CardMonopolyApiViewModel : BaseViewModel() {
    private val repo by lazy { CardMonopolyApiRepository() }

    private val shareModel by lazy { BaseUIModel<CommonShare>() }
    private val friendPocketModel by lazy { BaseUIModel<FriendPocket>() }
    private val myPocketModel by lazy { BaseUIModel<MyPocket>() }
    private val myPocketCardsModel by lazy { BaseUIModel<MyPocketCards>() }
    private val friendNormalCardsModel by lazy { BaseUIModel<FriendNormalCards>() }
    private val myPropCardsModel by lazy { BaseUIModel<MyPropCards>() }
    private val suitListModel by lazy { BaseUIModel<SuitList>() }
    private val allSuitListModel by lazy { BaseUIModel<SuitList>() }
    private val digBoxModel by lazy { BaseUIModel<DigBox>() }
    private val openBoxModel by lazy { BaseUIModel<OpenBox>() }
    private val pickCardFromMeModel by lazy { BaseUIModel<PickCardFromMe>() }
    private val pickCardFromFriendModel by lazy { BaseUIModel<PickCard>() }
    private val pickCardFromWishModel by lazy { BaseUIModel<CommResult>() }
    private val activeStrongBoxPositionModel by lazy { BaseUIModel<CommResult>() }
    private val unlockStrongBoxModel by lazy { BaseUIModel<CommResult>() }
    private val moveCardToStrongBoxModel by lazy { BaseUIModel<MoveCard>() }
    private val moveCardToPocketModel by lazy { BaseUIModel<MoveCard>() }
    private val mixSuitModel by lazy { BaseUIModel<MixSuit>() }
    private val discardModel by lazy { BaseUIModel<Discard>() }
    private val startTradeModel by lazy { BaseUIModel<CommResult>() }
    private val addFriendModel by lazy { BaseUIModel<CommResult>() }
    private val drawBoxModel by lazy { BaseUIModel<DrawBox>() }
    private val robotPocketModel by lazy { BaseUIModel<Robot>() }
    private val usePropCardModel by lazy { BaseUIModel<UseToolResult>() }
    private val suitCardsModel by lazy { BaseUIModel<SuitCards>() }
    private val suitDetailModel by lazy { BaseUIModel<SuitDetail>() }
    private val upgradeSuitModel by lazy { BaseUIModel<UpgradeSuit>() }
    private val querySuitByCardModel by lazy { BaseUIModel<QuerySuitList>() }
    private val myCardsBySuitModel by lazy { BaseUIModel<MyCardsBySuit>() }
    private val myWishModel by lazy { BaseUIModel<MyWish>() }
    private val friendWishModel by lazy { BaseUIModel<FriendWish>() }
    private val addOrChangeWishModel by lazy { BaseUIModel<CommResult>() }
    private val wishComeTrueModel by lazy { BaseUIModel<CommResult>() }
    private val updateSignatureModel by lazy { BaseUIModel<CommResult>() }
    private val signatureListModel by lazy { BaseUIModel<SignatureList>() }
    private val friendsModel by lazy { BaseUIModel<Friends>() }
    private val deleteFriendModel by lazy { BaseUIModel<CommResult>() }
    private val limitSuitUsersModel by lazy { BaseUIModel<LimitSuitUsers>() }
    private val auctionBuyModel by lazy { BaseUIModel<BuyList>() }
    private val auctionModel by lazy { BaseUIModel<AuctionList>() }
    private val boxCardModel by lazy { BaseUIModel<BoxList>() }
    private val propCardStoreModel by lazy { BaseUIModel<PropList>() }
    private val gameModel by lazy { BaseUIModel<RecordList>() }
    private val deleteRecordModel by lazy { BaseUIModel<CommResult>() }
    private val buyCardBoxModel by lazy { BaseUIModel<OpenBox>() }
    private val buyPropCardModel by lazy { BaseUIModel<CommResult>() }
    private val buyBatchPropCardModel by lazy { BaseUIModel<CommResult>() }
    private val retrieveCardModel by lazy { BaseUIModel<CommResult>() }
    private val cancelAuctionModel by lazy { BaseUIModel<CommResult>() }
    private val bidListModel by lazy { BaseUIModel<BidList>() }
    private val bidModel by lazy { BaseUIModel<CommResult>() }
    private val bidSuccessPickCardModel by lazy { BaseUIModel<CommResult>() }
    private val addAuctionModel by lazy { BaseUIModel<CommResult>() }
    private val tradeRaisePriceModel by lazy { BaseUIModel<CommResult>() }
    private val agreeAddFriendModel by lazy { BaseUIModel<CommResult>() }
    private val refuseAddFriendModel by lazy { BaseUIModel<CommResult>() }
    private val refuseTradeModel by lazy { BaseUIModel<CommResult>() }
    private val agreeTradeModel by lazy { BaseUIModel<CommResult>() }
    private val demonCardModel by lazy { BaseUIModel<BooleanResult>() }
    private val wishWallModel by lazy { BaseUIModel<WishWall>() }
    private val userDetailModel by lazy { BaseUIModel<UserDetail>() }
    private val currentIssueSuitListModel by lazy { BaseUIModel<CurrentIssueSuitList>() }
    private val suitShowModel by lazy { BaseUIModel<SuitShow>() }
    private val searchSuitListModel by lazy { BaseUIModel<SearchSuitList>() }

    val shareUiState by lazy { shareModel.uiState }
    val friendPocketUiState by lazy { friendPocketModel.uiState }
    val myPocketUiState by lazy { myPocketModel.uiState }
    val myPocketCardsUiState by lazy { myPocketCardsModel.uiState }
    val friendNormalCardsUiState by lazy { friendNormalCardsModel.uiState }
    val myPropCardsUiState by lazy { myPropCardsModel.uiState }
    val suitListUiState by lazy { suitListModel.uiState }
    val allSuitListUiState by lazy { allSuitListModel.uiState }
    val digBoxUiState by lazy { digBoxModel.uiState }
    val openBoxUiState by lazy { openBoxModel.uiState }
    val pickCardFromMeUiState by lazy { pickCardFromMeModel.uiState }
    val pickCardFromFriendUiState by lazy { pickCardFromFriendModel.uiState }
    val pickCardFromWishUiState by lazy { pickCardFromWishModel.uiState }
    val activeStrongBoxPositionUiState by lazy { activeStrongBoxPositionModel.uiState }
    val unlockStrongBoxUiState by lazy { unlockStrongBoxModel.uiState }
    val moveCardToStrongBoxUiState by lazy { moveCardToStrongBoxModel.uiState }
    val moveCardToPocketUiState by lazy { moveCardToPocketModel.uiState }
    val mixSuitUiState by lazy { mixSuitModel.uiState }
    val discardUiState by lazy { discardModel.uiState }
    val startTradeUiState by lazy { startTradeModel.uiState }
    val addFriendUiState by lazy { addFriendModel.uiState }
    val drawBoxModelUiState by lazy { drawBoxModel.uiState }
    val robotPocketUiState by lazy { robotPocketModel.uiState }
    val usePropCardUiState by lazy { usePropCardModel.uiState }
    val suitCardsUiState by lazy { suitCardsModel.uiState }
    val suitDetailUiState by lazy { suitDetailModel.uiState }
    val upgradeSuitUiState by lazy { upgradeSuitModel.uiState }
    val querySuitByCardUiState by lazy { querySuitByCardModel.uiState }
    val myCardsBySuitUiState by lazy { myCardsBySuitModel.uiState }
    val myWishUiState by lazy { myWishModel.uiState }
    val friendWishUiState by lazy { friendWishModel.uiState }
    val addOrChangeWishUiState by lazy { addOrChangeWishModel.uiState }
    val wishComeTrueUiState by lazy { wishComeTrueModel.uiState }
    val updateSignatureUiState by lazy { updateSignatureModel.uiState }
    val signatureListUiState by lazy { signatureListModel.uiState }
    val friendsUiState by lazy { friendsModel.uiState }
    val deleteFriendUiState by lazy { deleteFriendModel.uiState }
    val limitSuitUsersUiState by lazy { limitSuitUsersModel.uiState }
    val auctionBuyUiState by lazy { auctionBuyModel.uiState }
    val auctionUiState by lazy { auctionModel.uiState }
    val boxUiState by lazy { boxCardModel.uiState }
    val propCardStoreUiState by lazy { propCardStoreModel.uiState }
    val gameUiState by lazy { gameModel.uiState }
    val deleteRecordUiState by lazy { deleteRecordModel.uiState }
    val buyCardBoxUiState by lazy { buyCardBoxModel.uiState }
    val buyPropCardUiState by lazy { buyPropCardModel.uiState }
    val buyBatchPropCardUiState by lazy { buyBatchPropCardModel.uiState }
    val retrieveCardUiState by lazy { retrieveCardModel.uiState }
    val bidListUiState by lazy { bidListModel.uiState }
    val cancelAuctionUiState by lazy { cancelAuctionModel.uiState }
    val bidUiState by lazy { bidModel.uiState }
    val bidSuccessPickCardUiState by lazy { bidSuccessPickCardModel.uiState }
    val addAuctionUiState by lazy { addAuctionModel.uiState }
    val tradeRaisePriceUiState by lazy { tradeRaisePriceModel.uiState }
    val agreeAddFriendUiState by lazy { agreeAddFriendModel.uiState }
    val refuseAddFriendUiState by lazy { refuseAddFriendModel.uiState }
    val refuseTradeUiState by lazy { refuseTradeModel.uiState }
    val agreeTradeUiState by lazy { agreeTradeModel.uiState }
    val demonCardUiState by lazy { demonCardModel.uiState }
    val wishWallUiState by lazy { wishWallModel.uiState }
    val userDetailUiState by lazy { userDetailModel.uiState }
    val currentIssueSuitListUiState by lazy { currentIssueSuitListModel.uiState }
    val suitShowUiState by lazy { suitShowModel.uiState }
    val searchSuitListUiState by lazy { searchSuitListModel.uiState }


    /**
     * ???????????????????????????
     */
    fun getShareInfo() {
        call(shareModel) {
            repo.getShareInfo()
        }
    }

    /**
     * TA?????????
     */
    fun friendPocket(friendId: Long) {
        call(friendPocketModel) {
            repo.friendPocket(friendId)
        }
    }

    /**
     * ????????????
     */
    fun myPocket() {
        call(myPocketModel) {
            repo.myPocket()
        }
    }

    /**
     * ????????????????????????
     */
    fun myPocketCards() {
        call(myPocketCardsModel) {
            repo.myPocketCards()
        }
    }

    /**
     * ????????????????????????
     */
    fun friendNormalCards(friendId: Long) {
        call(friendNormalCardsModel) {
            repo.friendNormalCards(friendId)
        }
    }

    /**
     * ??????????????????????????????
     */
    fun friendLimitCards(friendId: Long) {
        call(friendNormalCardsModel) {
            repo.friendLimitCards(friendId)
        }
    }

    /**
     * ???????????????
     */
    fun myPropCards() {
        call(myPropCardsModel) {
            repo.myPropCards()
        }
    }

    /**
     * ??????????????????
     */
    fun hasDemonCard() {
        call(demonCardModel) {
            repo.hasDemonCard()
        }
    }

    /**
     * ???/TA?????????
     */
    fun suitList(
        userId: Long,
        categoryId: Long,
        pageIndex: Long = 1,
        pageSize: Long = 10L
    ) {
        call(
            uiModel = suitListModel,
            hasMore = {
                it.hasMore
            }
        ) {
            repo.suitList(userId, categoryId, pageIndex, pageSize)
        }
    }

    /**
     * ??????????????????
     * suitType: 1???????????????2????????????
     */
    fun allSuitList(
        suitType: Long = 1L,
        pageIndex: Long = 1L,
        pageSize: Long = 30L
    ) {
        call(
            uiModel = allSuitListModel,
            hasMore = {
                it.hasMore
            }
        ) {
            repo.allSuitList(suitType, pageIndex, pageSize)
        }
    }

    /**
     * ?????????
     */
    fun digBox() {
        call(digBoxModel) {
            repo.digBox()
        }
    }

    /**
     * ????????????
     */
    fun openBox(
        position: Long,
        openWithGold: Boolean = false
    ) {
        call(openBoxModel) {
            repo.openBox(position, openWithGold)
        }
    }

    /**
     * ?????????????????????????????????
     */
    fun pickCardFromMe(userCardId: String) {
        call(pickCardFromMeModel) {
            repo.pickCardFromMe(userCardId)
        }
    }

    /**
     * ?????????/????????????????????????????????????
     */
    fun pickCardFromFriend(
        userCardId: Long,
        friendId: Long,
        isRobot: Boolean
    ) {
        call(pickCardFromFriendModel) {
            repo.pickCardFromFriend(userCardId, friendId, isRobot)
        }
    }

    /**
     * ???????????????????????????????????????
     */
    fun batchPickCardFromMe(userCardIds: String) {
        call(pickCardFromMeModel) {
            repo.batchPickCardFromMe(userCardIds)
        }
    }

    /**
     * ?????????/??????????????????????????????????????????
     */
    fun batchPickCardFromFriend(
        userCardIds: String,
        friendId: Long,
        isRobot: Boolean
    ) {
        call(pickCardFromFriendModel) {
            repo.batchPickCardFromFriend(userCardIds, friendId, isRobot)
        }
    }

    /**
     * ????????????????????????
     */
    fun pickCardFromWish() {
        call(pickCardFromWishModel) {
            repo.pickCardFromWish()
        }
    }

    /**
     * ?????????????????????1-10
     */
    fun activeStrongBoxPosition(@IntRange(from = 0, to = 10) position: Int) {
        call(activeStrongBoxPositionModel) {
            repo.activeStrongBoxPosition(position)
        }
    }

    /**
     * ???????????????(1-10)
     */
    fun unlockStrongBox(@IntRange(from = 0, to = 10) position: Int) {
        call(unlockStrongBoxModel) {
            repo.unlockStrongBox(position)
        }
    }

    /**
     * ????????????????????????
     */
    fun moveCardToStrongBox(userCardIds: String) {
        call(moveCardToStrongBoxModel) {
            repo.moveCardToStrongBox(userCardIds)
        }
    }

    /**
     * ???????????????????????????
     */
    fun moveCardToPocket(userCardIds: String) {
        call(moveCardToPocketModel) {
            repo.moveCardToPocket(userCardIds)
        }
    }

    /**
     * ????????????
     */
    fun mixSuit(userCardIds: String) {
        call(mixSuitModel) {
            repo.mixSuit(userCardIds)
        }
    }

    /**
     * ????????????
     */
    fun discard(
        userCardIds: String,
        friendId: Long,
        isRobot: Boolean
    ) {
        call(discardModel) {
            repo.discard(userCardIds, friendId, isRobot)
        }
    }

    /**
     * ????????????
     */
    fun startTrade(
        friendId: Long,
        myCardId: Long,
        friendCardId: Long,
        gold: Long,
        message: String
    ) {
        call(startTradeModel) {
            repo.startTrade(friendId, myCardId, friendCardId, gold, message)
        }
    }

    /**
     * ????????????
     */
    fun addFriend(
        friendId: Long,
        message: String
    ) {
        call(addFriendModel) {
            repo.addFriend(friendId, message)
        }
    }

    /**
     * ??????????????????
     */
    fun drawBox(
        @IntRange(from = 0, to = 4) position: Int,
        cardIds: String
    ) {
        call(uiModel = drawBoxModel,
            isShowLoading = true,
            isEmpty = {
                false
            }
        ) {
            repo.drawBox(position, cardIds)
        }
    }

    /**
     * ???????????????
     */
    fun robotPocket(robotId: Long) {
        call(robotPocketModel) {
            repo.robotPocket(robotId)
        }
    }

    /**
     * ???????????????
     * @param cardToolId ?????????????????????Id
     * @param targetUserId ????????????Id??????????????????
     * @param targetToolId ???????????????Id???????????????????????????????????????
     * @param targetCardId ????????????Id???????????????????????????????????????
     * @param useDemonCard ????????????????????????????????????????????????????????????????????????
     */
    fun usePropCard(
        cardToolId: Long,
        targetUserId: Long,
        targetToolId: Long? = null,
        targetCardId: Long? = null,
        useDemonCard: Boolean? = null
    ) {
        call(usePropCardModel) {
            repo.usePropCard(cardToolId, targetUserId, targetToolId, targetCardId, useDemonCard)
        }
    }

    /**
     * ??????????????????
     */
    fun suitCards(
        suitId: Long
    ) {
        call(suitCardsModel) {
            repo.suitCards(suitId)
        }
    }

    /**
     * ??????????????????
     * [suitClass] ???????????????c,b,a
     */
    fun suitDetail(
        suitId: Long,
        suitClass: String,
        suitUserId: Long
    ) {
        call(suitDetailModel) {
            repo.suitDetail(suitId, suitClass, suitUserId)
        }
    }

    /**
     * ????????????
     * [suitClass] ???????????????c,b,a
     */
    fun upgradeSuit(
        suitId: Long,
        suitClass: String
    ) {
        call(upgradeSuitModel) {
            repo.upgradeSuit(suitId, suitClass)
        }
    }

    /**
     * ???????????????????????????
     * [getCount] ?????????????????????10???
     */
    fun querySuitByCard(
        suitName: String,
        getCount: Long = 10L,
        isShowLoading: Boolean
    ) {
        call(
            uiModel = querySuitByCardModel,
            isShowLoading = isShowLoading
        ) {
            repo.querySuitByCard(suitName, getCount)
        }
    }

    /**
     * ??????????????????????????????
     */
    fun myCardBySuit(
        suitId: Long
    ) {
        call(myCardsBySuitModel) {
            repo.myCardBySuit(suitId)
        }
    }

    /**
     * ????????????
     */
    fun myWish() {
        call(myWishModel) {
            repo.myWish()
        }
    }

    /**
     * TA?????????
     */
    fun friendWish(friendId: Long) {
        call(friendWishModel) {
            repo.friendWish(friendId)
        }
    }

    /**
     * ?????????
     */
    fun wishWall(pageIndex: Long,pageSize: Long) {
        call(wishWallModel) {
            repo.wishWall(pageIndex,pageSize)
        }
    }

    /**
     * ?????????????????????
     */
    fun addOrChangeWish(
        cardId: Long,
        content: String
    ) {
        call(addOrChangeWishModel) {
            repo.addOrChangeWish(cardId, content)
        }
    }

    /**
     * ???TA????????????
     */
    fun wishComeTrue(friendId: Long) {
        call(wishComeTrueModel) {
            repo.wishComeTrue(friendId)
        }
    }

    /**
     * ???????????????
     */
    fun updateSignature(signature: String) {
        call(updateSignatureModel) {
            repo.updateSignature(signature)
        }
    }

    /**
     * ????????????????????????
     * [getCount] ?????????????????????20???
     */
    fun signatureList(getCount: Long = 200L) {
        call(signatureListModel) {
            repo.signatureList(getCount)
        }
    }


    /**
     * ????????????
     */
    fun friends(
        orderType: Long,
        friendName: String? = "",
        pageIndex: Long,
        pageSize: Long = 20L
    ) {
        call(
            uiModel = friendsModel,
            hasMore = {
                it.hasMore
            }
        ) {
            repo.friends(orderType, friendName, pageIndex, pageSize)
        }
    }

    /**
     * ????????????
     */
    fun deleteFriend(friendId: Long) {
        call(deleteFriendModel) {
            repo.deleteFriend(friendId)
        }
    }

    /**
     * ??????????????????????????????
     * @param suitId ??????id
     */
    fun loadSuitRankList(suitId: Long) {
        call(
            uiModel = limitSuitUsersModel,
            isEmpty = {
                it.maxinumUser == null
            }
        ) {
            repo.limitSuitUsers(suitId)
        }
    }

    /**
     * ???????????????????????????
     * @param orderType 1?????????????????????2?????????????????????3??????????????????4??????????????????5??????????????????6???????????????
     * @param queryType  ???????????? 1?????????2????????????3??????
     * @param cardId ???????????????Id
     */
    fun loadAuctionBuyList(
        orderType: Long,
        queryType: Long,
        cardId: Long,
        pageIndex: Long,
        pageSize: Long
    ) {
        call(
            uiModel = auctionBuyModel,
            isEmpty = {
                it.buyList?.isEmpty() == true && pageIndex == 1L
            }
        ) {
            repo.loadAuctionBuyList(orderType, queryType, cardId, pageIndex, pageSize)
        }
    }

    /**
     * ????????????????????????????????????
     */
    fun loadAuctionList(
        pageIndex: Long,
        pageSize: Long
    ) {
        call(
            uiModel = auctionModel,
            isEmpty = {
                pageIndex == 1L && it.auctionList?.isEmpty() == true
            },
            hasMore = {
                it.hasMore
            }
        ) {
            repo.loadAuctionList(pageIndex, pageSize)
        }
    }

    /**
     * ????????????????????????
     */
    fun loadBoxList() {
        call(
            uiModel = boxCardModel,
            isEmpty = {
                it.commonBoxList?.isNullOrEmpty() == true &&
                        it.activityBoxList?.isNullOrEmpty() == true &&
                        it.boughtBox == null &&
                        it.limitBoxList?.isNullOrEmpty() == true
            }
        ) {
            repo.loadBoxList()
        }
    }

    /**
     * ???????????????????????????
     */
    fun loadPropCardList() {
        call(
            uiModel = propCardStoreModel,
            isEmpty = {
                it.toolCardList?.isNullOrEmpty() == true
            }
        ) {
            repo.loadPropCardList()
        }
    }

    /**
     * ??????????????????
     */
    fun loadGameInfo(typeId: Long, pageIndex: Long, pageSize: Long, type:Long) {
        call(gameModel) {
            repo.loadGameInfoList(typeId, pageIndex, pageSize, type)
        }
    }

    /**
     * ????????????
     */
    fun deleteRecord(recordDetailId: Long) {
        call(deleteRecordModel) {
            repo.deleteRecord(recordDetailId)
        }
    }


    /**
     * ????????????
     */
    fun buyCardBox(cardBoxId: Long) {
        call(buyCardBoxModel) {
            repo.buyCardBox(cardBoxId)
        }
    }

    /**
     * ???????????????
     */
    fun buyPropCard(propId: Long) {
        call(buyPropCardModel) {
            repo.buyPropCard(propId)
        }
    }

    /**
     * ???????????????
     */
    fun buyBatchPropCard(propId: Long,num:Long) {
        call(buyPropCardModel) {
            repo.buyBatchPropCard(propId,num)
        }
    }

    /**
     * ??????????????????
     */
    fun retrieveCard(cardId: Long) {
        call(retrieveCardModel) {
            repo.retrieveCard(cardId)
        }
    }

    /**
     * ????????????
     */
    fun cancelAuction(cardId: Long) {
        call(cancelAuctionModel) {
            repo.cancelAuction(cardId)
        }
    }

    /**
     * ???????????????
     */
    fun getAuctionBidList(pageIndex: Long) {
        call(bidListModel) {
            repo.getBidList(pageIndex)
        }
    }

    /**
     * ????????????
     * @param auctionId ??????id
     * @param bidPrice ??????
     */
    fun bid(auctionId: Long, bidPrice: Long) {
        call(bidModel) {
            repo.bid(auctionId, bidPrice)
        }
    }

    /**
     * ?????????
     * @param auctionId ??????id
     */
    fun bidSuccessPickCard(auctionId: Long) {
        call(bidSuccessPickCardModel) {
            repo.bidSuccessPickCard(auctionId)
        }
    }

    /**
     * ????????????
     */
    fun addAuction(
        id: Long? = null,
        type: Long? = null,
        timeLimited: Long,
        startPrice: Long,
        fixPrice: Long
    ) {
        call(addAuctionModel) {
            repo.addAuction(id, type, timeLimited, startPrice, fixPrice)
        }
    }

    /**
     * ????????????
     */
    fun addTradePrice(recordDetailId: Long, raiseGolds: Long, message: String) {
        call(tradeRaisePriceModel) {
            repo.addTradePrice(recordDetailId, raiseGolds, message)
        }
    }

    /**
     * ??????????????????
     */
    fun agreeFriend(recordDetailId: Long) {
        call(agreeAddFriendModel) {
            repo.agreeAddFriend(recordDetailId)
        }
    }


    /**
     * ??????????????????
     */
    fun refuseFriend(recordDetailId: Long, message: String) {
        call(refuseAddFriendModel) {
            repo.refuseAddFriend(recordDetailId, message)
        }
    }

    /**
     * ????????????
     */
    fun refuseTrade(recordDetailId: Long, message: String) {
        call(refuseTradeModel) {
            repo.refuseTrade(recordDetailId, message)
        }
    }

    /**
     * ????????????
     */
    fun agreeTrade(recordDetailId: Long) {
        call(agreeTradeModel) {
            repo.agreeTrade(recordDetailId)
        }
    }

    /**
     * ??????????????????
     */
    fun userDetail(userId: Long) {
        call(userDetailModel) {
            repo.userDetail(userId)
        }
    }


    /**
     * ????????????
     */
    fun currentIssueSuitList() {
        call(currentIssueSuitListModel) {
            repo.currentIssueSuitList()
        }
    }

    /**
     * ????????????
     */
    fun suitShow(
        cardUserSuitId: Long,
        suitId: Long
    ) {
        call(suitShowModel) {
            repo.suitShow(cardUserSuitId, suitId)
        }
    }

    /**
     * ??????????????????????????????
     */
    fun searchSuitList(
        userId: Long,
        suitId: Long
    ) {
        call(searchSuitListModel) {
            repo.searchSuitList(userId, suitId)
        }
    }

}
