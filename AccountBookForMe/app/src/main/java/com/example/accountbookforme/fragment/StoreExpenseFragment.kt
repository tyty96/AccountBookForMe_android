package com.example.accountbookforme.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbookforme.activity.DetailActivity
import com.example.accountbookforme.adapter.ExpenseStoreListAdapter
import com.example.accountbookforme.databinding.FragmentListWithTitleBinding
import com.example.accountbookforme.model.Expense
import com.example.accountbookforme.viewmodel.ExpensesViewModel

class StoreExpenseFragment : Fragment() {

    private var _binding: FragmentListWithTitleBinding? = null
    private val binding get() = _binding!!

    private val expensesViewModel: ExpensesViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseStoreListAdapter: ExpenseStoreListAdapter

    private var storeId: Long = 0
    private lateinit var storeName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentListWithTitleBinding.inflate(inflater, container, false)
        val view = binding.root

        // リストのタイトルを表示
        binding.listTitle.text = "Expenses"

        recyclerView = binding.listWithTitle
        expenseStoreListAdapter = ExpenseStoreListAdapter()

        // クリックした品物が登録されている支出詳細を表示する
        expenseStoreListAdapter.setOnExpenseClickListener(
            object : ExpenseStoreListAdapter.OnExpenseClickListener {
                override fun onItemClick(expense: Expense) {

                    val intent = Intent(context, DetailActivity::class.java)
                    // 支出IDを渡す
                    intent.putExtra("expenseId", expense.id)
                    // 支出詳細画面に遷移する
                    startActivity(intent)
                }
            }
        )

        val linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = expenseStoreListAdapter

        // セルの区切り線表示
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                view.context,
                linearLayoutManager.orientation
            )
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 前画面から渡された支出IDを取得
        val bundle = arguments
        if (bundle != null) {
            storeId = bundle.getLong("storeId")
            storeName = bundle.getString("storeName").toString()
        }

        // アクションバーのタイトルを設定
        (activity as AppCompatActivity).supportActionBar?.title = storeName

        // 品物リスト取得
        expensesViewModel.getExpenseListByStoreId(storeId)

        // 支出リストの監視開始
        expensesViewModel.expenseStoreList.observe(viewLifecycleOwner, { expenseStoreList ->
            expenseStoreListAdapter.setExpenseStoreListItems(expenseStoreList)
            // 総額を表示
            binding.allTotal.text = expensesViewModel.calcTotalByStore().toString()
        })
    }

    // メニュータップ時の処理設定
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
            }
        }
        return true
    }
}