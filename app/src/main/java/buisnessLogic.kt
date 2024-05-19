class buisnessLogic(aAmount: Double, aTipPercent: Double, aNumberOfPeople: UInt = 1U) {
    private var mAmount: Double = aAmount
    private var mTipPercent: Double = aTipPercent
    private var mNumberOfPeople: UInt = aNumberOfPeople

    public fun getAmount(): Double {
        return mAmount
    }

    public fun setAmount(aAmount: Double) {
        mAmount = aAmount
    }

    public fun getTipPercent(): Double {
        return mTipPercent
    }

    public fun setTipPercent(aTip: Double) {
        mTipPercent = aTip
    }

    public fun getNumberOfPeople(): UInt {
        return mNumberOfPeople
    }

    public fun setNumberOfPeople(aNumberOfPeople: UInt) {
        mNumberOfPeople = aNumberOfPeople
    }

    public fun getTotalWithTip(): Double {
        return (mAmount * mTipPercent / 100.0) + mAmount
    }

    public fun getTotalWithTipPerPerson(): Double {
        return if (mNumberOfPeople != 0U) {
            getTotalWithTip() / mNumberOfPeople.toDouble()
        } else {
            getTotalWithTip()
        }
    }

    public fun setTipPercentViaTotalTip(aNewTotalWithTip: Double) {
        mTipPercent = (aNewTotalWithTip - mAmount) * 100.0 / mAmount
    }
}