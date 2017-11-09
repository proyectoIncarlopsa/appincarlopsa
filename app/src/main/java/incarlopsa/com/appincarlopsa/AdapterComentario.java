package incarlopsa.com.appincarlopsa;

import android.content.res.Resources;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.ViewHolder> implements ICodigos{

    private ArrayList<DataBaseItem> listaComentarios;
    private SingleCredenciales credenciales = SingleCredenciales.getInstance();
    private HiloParaRead hiloParaRead;
    private ArrayList<DataBaseItem> resultadosUsuarios;
    private SingleTostada tostada = SingleTostada.getInstance();

    public AdapterComentario(ArrayList<DataBaseItem> listaCabecera) {
        this.listaComentarios = listaCabecera;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comentario, parent, false);


        final ViewHolder viewHolder = new ViewHolder(v);

        final ImageView botonLike = (ImageView)v.findViewById(R.id.btnComentarioLike);
        botonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View meGusta) {
                Integer id = meGusta.getId();
                int position = viewHolder.getAdapterPosition();
                if(mListener!=null) {
                    mListener.onItemClick(listaComentarios.get(position), position);
                }

                Comentario comentarioAux = (Comentario)listaComentarios.get(position);
                ArrayList<DataBaseItem> likes = comentarioAux.getArrayLikes();
                //Verificar que no he votado ya
                //1.- Recorrer el Array y ver si estoy
                MeAlgo meAlgo;
                Boolean todoOk = true;
                for(DataBaseItem m : likes){
                    meAlgo = (MeAlgo)m;
                    if (meAlgo.getIdUsuario() == credenciales.getIdUsuario()){ //Ya he votado, impedir!
                        todoOk = false;
                        break;
                    }
                }
                if (todoOk){
                    MeGusta meGustaAux = new MeGusta();
                    meGustaAux.setIdUsuario(credenciales.getIdUsuario());
                    meGustaAux.setIdComentario(comentarioAux.getId());
                    HiloParaCreate hiloParaCreate = new HiloParaCreate(new DAOLikes());
                    try {
                        todoOk = hiloParaCreate.execute(meGustaAux).get();
                        viewHolder.fotoMeGusta.setImageDrawable(viewHolder.recursos.getDrawable(R.drawable.likeverde));
                        viewHolder.fotoMeDisgusta.setEnabled(false);
                        viewHolder.fotoMeGusta.setEnabled(false);
                        Integer num = Integer.parseInt(viewHolder.numLikes.getText().toString());
                        num++;
                        viewHolder.numLikes.setText(num.toString());
                    } catch (Exception e) {
                        tostada.errorConexionBBDD();
                    }
                }
            }
        });


        final ImageView botonDislike = (ImageView)v.findViewById(R.id.btnComentarioDislike);
        botonDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View meDisgusta) {
                Integer id = meDisgusta.getId();
                int position = viewHolder.getAdapterPosition();
                if(mListener!=null) {
                    mListener.onItemClick(listaComentarios.get(position), position);
                }

                Comentario comentarioAux = (Comentario)listaComentarios.get(position);
                ArrayList<DataBaseItem> likes = comentarioAux.getArrayLikes();
                //Verificar que no he votado ya
                //1.- Recorrer el Array y ver si estoy
                MeAlgo meAlgo;
                Boolean todoOk = true;
                for(DataBaseItem m : likes){
                    meAlgo = (MeAlgo)m;
                    if (meAlgo.getIdUsuario() == credenciales.getIdUsuario()){ //Ya he votado, impedir!
                        todoOk = false;
                        break;
                    }
                }
                if (todoOk){
                    MeDisgusta meDisgustaAux = new MeDisgusta();
                    meDisgustaAux.setIdUsuario(credenciales.getIdUsuario());
                    meDisgustaAux.setIdComentario(comentarioAux.getId());
                    HiloParaCreate hiloParaCreate = new HiloParaCreate(new DAOLikes());
                    try {
                        todoOk = hiloParaCreate.execute(meDisgustaAux).get();
                        viewHolder.fotoMeDisgusta.setImageDrawable(viewHolder.recursos.getDrawable(R.drawable.dislikerojo));
                        viewHolder.fotoMeDisgusta.setEnabled(false);
                        viewHolder.fotoMeGusta.setEnabled(false);
                        Integer num = Integer.parseInt(viewHolder.numDislikes.getText().toString());
                        num++;
                        viewHolder.numDislikes.setText(num.toString());
                    } catch (Exception e) {
                        tostada.errorConexionBBDD();
                    }
                }
            }

        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comentario comentario = (Comentario) listaComentarios.get(position);


        //Recuperar el usuario que ha creado el comentario
        ArrayList<DataBaseItem> resultados = new ArrayList<>();

        //Recuperar el nombre del usuario
        String nombreUsuario;
        if (comentario.getIdUsuario() == credenciales.getIdUsuario()){
            nombreUsuario = credenciales.getNombre()+" "+credenciales.getApellidos();
        }else{ //Por si acaso
            Usuario usuarioAux = new Usuario();
            usuarioAux.setIdUsuario(comentario.getIdUsuario());
            hiloParaRead = new HiloParaRead(new DAOUsuario());
            try {
                resultadosUsuarios = hiloParaRead.execute(usuarioAux).get();
                if (resultadosUsuarios.size()>0){
                    usuarioAux = (Usuario)resultadosUsuarios.get(0);
                    nombreUsuario = usuarioAux.getNombre()+" "+usuarioAux.getApellidos();
                }else{
                    nombreUsuario = USUARIO_DESCONOCIDO;
                }
            } catch (Exception e) {
                tostada.errorConexionBBDD();
                nombreUsuario = USUARIO_DESCONOCIDO;
            }
        }

        //Comprobar si se ha votado MeGusta o MeDisgusta y en caso afirmativo colorearlo
        ArrayList<DataBaseItem> likes = comentario.getArrayLikes();
        MeAlgo meAlgo;
        for(DataBaseItem m : likes){
            meAlgo = (MeAlgo)m;
            if (meAlgo.getIdUsuario() == credenciales.getIdUsuario()){ //Ya he votado! ver que tipo es
                if (meAlgo instanceof MeGusta){ // MeGusta
                    holder.fotoMeGusta.setImageDrawable(holder.recursos.getDrawable(R.drawable.likeverde));
                    holder.fotoMeDisgusta.setEnabled(false);
                    holder.fotoMeGusta.setEnabled(false);
                }else{ //NoMegusta
                    holder.fotoMeDisgusta.setImageDrawable(holder.recursos.getDrawable(R.drawable.dislikerojo));
                    holder.fotoMeDisgusta.setEnabled(false);
                    holder.fotoMeGusta.setEnabled(false);
                }
            }
        }


        //Resto de asignaciones
        String fecha = comentario.getFecha();
        String hora = comentario.getHora();
        String comentarioDicho = comentario.getMensaje();
        String numLikes = comentario.getNumeroMeGusta().toString();
        String numDislikes = comentario.getNumeroMeDisgusta().toString();

        holder.nombreUsuario.setText(nombreUsuario);
        holder.fecha.setText(fecha);
        holder.hora.setText(hora);
        holder.comentarioDicho.setText(comentarioDicho);
        holder.numLikes.setText(numLikes);
        holder.numDislikes.setText(numDislikes);

    }

    public void actualizar(ArrayList<DataBaseItem> nuevaLista){
        listaComentarios.clear();
        listaComentarios.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsuario;
        private TextView fecha;
        private TextView hora;
        private TextView comentarioDicho;
        private TextView numLikes;
        private TextView numDislikes;
        private ImageView fotoMeGusta;
        private ImageView fotoMeDisgusta;
        private View v;
        private Resources recursos;

        public ViewHolder(View v) {
            super(v);
            nombreUsuario = (TextView) v.findViewById(R.id.txtComentarioUsuario);
            fecha = (TextView) v.findViewById(R.id.txtComentarioFecha);
            hora = (TextView) v.findViewById(R.id.txtComentarioHora);
            comentarioDicho = (TextView) v.findViewById(R.id.txtComentarioComentario);
            numLikes = (TextView) v.findViewById(R.id.txtComentarioNumLikes);
            numDislikes = (TextView) v.findViewById(R.id.txtComentarioNumDislikes);
            fotoMeGusta = (ImageView) v.findViewById(R.id.btnComentarioLike);
            fotoMeDisgusta = (ImageView) v.findViewById(R.id.btnComentarioDislike);
            this.v = v;
            recursos = v.getResources();
        }
    }

    public Integer ultimaPosicion(){
        Integer retorno = 0;
        if (listaComentarios.size()>0){
            retorno = listaComentarios.size()-1;
        }
        return retorno;
    }

    public interface OnItemClickListener {
        public void onItemClick(DataBaseItem item, int position);
    }
    private AdapterComentario.OnItemClickListener mListener;

    public void setOnItemListener(AdapterComentario.OnItemClickListener listener) {
        mListener = listener;
    }

}