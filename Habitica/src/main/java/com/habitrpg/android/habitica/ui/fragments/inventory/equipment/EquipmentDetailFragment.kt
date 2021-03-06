package com.habitrpg.android.habitica.ui.fragments.inventory.equipment

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.habitrpg.android.habitica.R
import com.habitrpg.android.habitica.components.AppComponent
import com.habitrpg.android.habitica.data.InventoryRepository
import com.habitrpg.android.habitica.helpers.RxErrorHandler
import com.habitrpg.android.habitica.models.inventory.Equipment
import com.habitrpg.android.habitica.models.user.Items
import com.habitrpg.android.habitica.ui.adapter.inventory.EquipmentRecyclerViewAdapter
import com.habitrpg.android.habitica.ui.fragments.BaseMainFragment
import com.habitrpg.android.habitica.ui.helpers.SafeDefaultItemAnimator
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import rx.functions.Action1
import javax.inject.Inject

class EquipmentDetailFragment : BaseMainFragment() {

    @Inject
    lateinit var inventoryRepository: InventoryRepository

    var type: String? = null
    var equippedGear: String? = null
    var isCostume: Boolean? = null

    private var adapter: EquipmentRecyclerViewAdapter = EquipmentRecyclerViewAdapter(null, true)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_recyclerview, container, false)

        this.adapter.equippedGear = this.equippedGear
        this.adapter.isCostume = this.isCostume
        this.adapter.type = this.type
        this.adapter.equipEvents.flatMap<Items> { key -> inventoryRepository.equipGear(user, key, isCostume!!) }
                .subscribe(Action1 { }, RxErrorHandler.handleEmptyError())
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = this.adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(getActivity()!!, DividerItemDecoration.VERTICAL))
        recyclerView.itemAnimator = SafeDefaultItemAnimator()

        inventoryRepository.getOwnedEquipment(type).first().subscribe(Action1<RealmResults<Equipment>> { this.adapter.updateData(it) }, RxErrorHandler.handleEmptyError())
    }

    override fun onDestroy() {
        inventoryRepository.close()
        super.onDestroy()
    }

    override fun injectFragment(component: AppComponent) {
        component.inject(this)
    }
}
