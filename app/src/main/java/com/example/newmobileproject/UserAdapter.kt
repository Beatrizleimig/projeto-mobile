import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newmobileproject.R
import com.example.newmobileproject.User

class UserAdapter(private val users: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Classe ViewHolder para representar cada item da lista
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tv_user_name)  // Nome do usuário
        val scoreTextView: TextView = itemView.findViewById(R.id.tv_user_score)  // Pontuação do usuário
    }

    // Método chamado para inflar o layout do item do RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)  // Retorna o ViewHolder com a view inflada
    }

    // Método chamado para associar os dados do usuário com os elementos da view
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]  // Obtém o usuário na posição atual
        holder.nameTextView.text = user.name  // Define o nome do usuário
        holder.scoreTextView.text = "Pontuação: ${user.score}"  // Define a pontuação do usuário
    }

    // Método que retorna a quantidade total de itens (usuários) no RecyclerView
    override fun getItemCount(): Int = users.size
}
