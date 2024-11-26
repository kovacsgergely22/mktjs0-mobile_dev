package hu.aut.android.costaccountant.touch

interface ShoppingTouchHelperAdapter {

    fun onItemDismissed(position: Int)

    fun onItemMoved(fromPosition: Int, toPosition: Int)
}