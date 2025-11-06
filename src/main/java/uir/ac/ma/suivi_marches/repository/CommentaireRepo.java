package uir.ac.ma.suivi_marches.repository;

import uir.ac.ma.suivi_marches.model.Commentaire;

import java.util.List;

public interface CommentaireRepo {
    List<Commentaire> getAllCommentaire();

    Commentaire getCommentaireById(int idCommentaire);

    void addCommentaire(Commentaire commentaire);

    void modifyCommentaire(Commentaire commentaire);

    void deleteCommentaire(int idCommentaire);
}