package org.darccona;

import org.darccona.database.*;
import org.darccona.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class NoteController {

    @Autowired
    UserRepository repository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    NoteRepository noteRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private SimpleDateFormat formatNow = new SimpleDateFormat("dd.MM.yy HH:mm");

    @GetMapping("/")
    public String start(Model model) {
        return "redirect:http://localhost:8080/notepad";
    }

    @GetMapping("/registration")
    public String formReg(Model model) {
        model.addAttribute("divError", false);
        model.addAttribute("reg",  new RegEntity());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("reg") RegEntity reg,
                               Model model) {
        UserEntity user = repository.findByName(reg.getName());
        if (user == null) {
            if ((reg.getName().equals("") || reg.getPassword1().equals("") || reg.getPassword2().equals(""))) {
                model.addAttribute("divError", true);
                model.addAttribute("error", "Все поля должны быть заполнены");
                model.addAttribute("reg", new RegEntity());
                return "registration";
            }
            if (!reg.getPassword1().equals(reg.getPassword2())) {
                model.addAttribute("divError", true);
                model.addAttribute("error", "Пароли не совпадают");
                model.addAttribute("reg", new RegEntity());
                return "registration";
            }
            repository.save(new UserEntity(reg.getName(), bCryptPasswordEncoder.encode(reg.getPassword1())));
        } else {
            model.addAttribute("divError", true);
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            model.addAttribute("reg", new RegEntity());
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:http://localhost:8080/login";
    }

    @RequestMapping("/notepad")
    public String notepadStart(@RequestParam(value = "id1", required = false, defaultValue = "-1") int id1,
                               @RequestParam(value = "id2", required = false, defaultValue = "-1") int id2,
                               Principal principal,
                               Model model) {
        UserEntity user = repository.findByName(principal.getName());
        DivEntity d = new DivEntity("ShowNote");
        Set<FolderEntity> folder = user.getFolder();
        if (folder.isEmpty()) {
            d.setInput();
            d.setFolder();
            d.setNote();
        } else {

            List<FolderEntity> folderList = new ArrayList<>();
            folderList.addAll(folder);
            Collections.sort(folderList, FolderEntity.COMPARE_BY_NAME);

            model.addAttribute("folder", folderList);
            if (id1 >= 0) {
                FolderEntity f2 = folderRepository.findById(id1);
                if ((f2 == null) || (f2.getUser() != user)) {
                    return "nope";
                }
                model.addAttribute("folderId", id1);
                FolderEntity f1 = folderRepository.findByUserAndStanding(user, true);
                model.addAttribute("nameFolder", new StringEntity(f2.getName()));
                if (f1 == f2) {
                    if (id2 >= 0) {
                        NoteEntity n = noteRepository.findById(id2);
                        if ((n == null) || (n.getFolder() != f2)) {
                            return "nope";
                        }
                        NoteEntity n1 = noteRepository.findByStandingAndFolder_UserIs(true, user);
                        if (n1 != null) {
                            n1.setStanding(false);
                            noteRepository.save(n1);
                        }
                        n.setStanding(true);
                        noteRepository.save(n);
                        model.addAttribute("noteId", id2);
                        Set<NoteEntity> note = f2.getNote();

                        List<NoteEntity> noteList = new ArrayList<>();
                        noteList.addAll(note);
                        noteList.sort(Comparator.comparing(NoteEntity::getName).thenComparing(NoteEntity::getDate));
                        model.addAttribute("note", noteList);

                        model.addAttribute("n", new RecordEntity(n.getName(), formatNow.format(n.getDate()), n.getRecord()));
                    } else {
                        NoteEntity n1 = noteRepository.findByStandingAndFolder_UserIs(true, user);
                        if (n1 != null) {
                            n1.setStanding(false);
                            noteRepository.save(n1);
                        }
                        f2.setStanding(false);
                        d.setInput();
                        d.setNote();
                    }
                } else {
                    if (f1 != null) {
                        f1.setStanding(false);
                        folderRepository.save(f1);
                    }
                    f2.setStanding(true);
                    if (!f2.getNote().isEmpty()) {
                        Set<NoteEntity> note = f2.getNote();

                        List<NoteEntity> noteList = new ArrayList<>();
                        noteList.addAll(note);
                        noteList.sort(Comparator.comparing(NoteEntity::getName).thenComparing(NoteEntity::getDate));

                        model.addAttribute("note", noteList);
                    } else {
                        d.setDivNote();
                    }
                    d.setInput();
                }
                folderRepository.save(f2);
            } else {
                d.setInput();
                d.setNote();
            }
        }

        model.addAttribute("userName", principal.getName());
        model.addAttribute("d", d);
        model.addAttribute("s", new StringEntity(""));
        return "notepad";
    }

    @RequestMapping("/notepad/editRecord")
    public String notepadEditRecord(@RequestParam(value = "id1") int id1,
                                    @RequestParam(value = "id2") int id2,
                                    Principal principal,
                                    Model model) {
        UserEntity user = repository.findByName(principal.getName());

        Set<FolderEntity> folder = user.getFolder();
        List<FolderEntity> folderList = new ArrayList<>();
        folderList.addAll(folder);
        Collections.sort(folderList, FolderEntity.COMPARE_BY_NAME);
        model.addAttribute("folder", folderList);
        model.addAttribute("folderId", id1);

        FolderEntity f2 = folderRepository.findById(id1);
        if ((f2 == null) || (f2.getUser() != user)) {
            return "nope";
        }
        Set<NoteEntity> note = f2.getNote();
        List<NoteEntity> noteList = new ArrayList<>();
        noteList.addAll(note);
        noteList.sort(Comparator.comparing(NoteEntity::getName).thenComparing(NoteEntity::getDate));
        model.addAttribute("note", noteList);
        model.addAttribute("noteId", id2);

        model.addAttribute("d", new DivEntity("EditNote"));
        model.addAttribute("s", new StringEntity(""));
        NoteEntity n = noteRepository.findById(id2);
        if ((n == null) || (n.getFolder() != f2)) {
            return "nope";
        }

        model.addAttribute("userName", principal.getName());
        model.addAttribute("n", new RecordEntity(n.getName(), formatNow.format(n.getDate()), n.getRecord()));
        model.addAttribute("nameFolder", new StringEntity(f2.getName()));

        return "notepad";
    }

    @PostMapping("/notepad/editRecord")
    public String recordEdit(@ModelAttribute("n") RecordEntity n,
                             @RequestParam(value = "id1") int id1,
                             @RequestParam(value = "id2") int id2,
                             Principal principal,
                             Model model) {
        Date date = new Date();
        NoteEntity now = noteRepository.findById(id2);
        if ((now == null) || (!now.getFolder().getUser().getName().equals(principal.getName()))) {
            return "nope";
        }
        if (n.getName().equals("")) {
            now.setName("Без имени");
        } else {
            now.setName(n.getName());
        }
        now.setRecord(n.getRecord());
        now.setDate(date);
        noteRepository.save(now);
        String url = "http://localhost:8080/notepad?id1=" + id1 + "&id2=" + id2;
        return "redirect:" + url;
    }

    @RequestMapping("/notepad/editFolder")
    public String notepadEditFolder(@RequestParam(value = "id1") int id1,
                                    Principal principal,
                                    Model model) {
        UserEntity user = repository.findByName(principal.getName());

        Set<FolderEntity> folder = user.getFolder();
        List<FolderEntity> folderList = new ArrayList<>();
        folderList.addAll(folder);
        Collections.sort(folderList, FolderEntity.COMPARE_BY_NAME);
        model.addAttribute("folder", folderList);
        model.addAttribute("folderId", id1);

        FolderEntity f2 = folderRepository.findById(id1);
        if ((f2 == null) || (f2.getUser() != user)) {
            return "nope";
        }
        NoteEntity n1 = noteRepository.findByStandingAndFolder_UserIs(true, user);
        if (n1 != null) {
            n1.setStanding(false);
            noteRepository.save(n1);
        }

        DivEntity d = new DivEntity("EditFolder");
        Set<NoteEntity> note = f2.getNote();
        if (note.isEmpty()) {
            d.setDivNote();
        } else {
            List<NoteEntity> noteList = new ArrayList<>();
            noteList.addAll(note);
            noteList.sort(Comparator.comparing(NoteEntity::getName).thenComparing(NoteEntity::getDate));
            model.addAttribute("note", noteList);
        }

        model.addAttribute("d", d);
        model.addAttribute("n", new StringEntity(f2.getName()));
        model.addAttribute("userName", principal.getName());
        model.addAttribute("s", new StringEntity(""));

        return "notepad";
    }

    @PostMapping("/notepad/editFolder")
    public String folderEdit(@ModelAttribute("n") StringEntity n,
                             @RequestParam(value = "id1") int id1,
                             Principal principal,
                             Model model) {
        FolderEntity now = folderRepository.findById(id1);
        if ((now == null) || (!now.getUser().getName().equals(principal.getName()))) {
            return "nope";
        }
        now.setStanding(false);
        if (n.getName().equals("")) {
            now.setName("Без имени");
        } else {
            now.setName(n.getName());
        }
        folderRepository.save(now);
        String url = "http://localhost:8080/notepad?id1=" + id1;
        return "redirect:" + url;
    }

    @RequestMapping("/notepad/addRecord")
    public String notepadAddRecord(@RequestParam(value = "id1") int id1,
                                   Principal principal,
                                   Model model) {
        UserEntity user = repository.findByName(principal.getName());

        Set<FolderEntity> folder = user.getFolder();
        List<FolderEntity> folderList = new ArrayList<>();
        folderList.addAll(folder);
        Collections.sort(folderList, FolderEntity.COMPARE_BY_NAME);
        model.addAttribute("folder", folderList);
        model.addAttribute("folderId", id1);

        FolderEntity f2 = folderRepository.findById(id1);
        if ((f2 == null) || (f2.getUser() != user)) {
            return "nope";
        }
        NoteEntity n1 = noteRepository.findByStandingAndFolder_UserIs(true, user);
        if (n1 != null) {
            n1.setStanding(false);
            noteRepository.save(n1);
        }

        DivEntity d = new DivEntity("AddNote");
        Set<NoteEntity> note = f2.getNote();
        if (note.isEmpty()) {
            d.setDivNote();
        } else {
            List<NoteEntity> noteList = new ArrayList<>();
            noteList.addAll(note);
            noteList.sort(Comparator.comparing(NoteEntity::getName).thenComparing(NoteEntity::getDate));
            model.addAttribute("note", noteList);
        }

        model.addAttribute("d", d);
        model.addAttribute("s", new StringEntity(""));
        model.addAttribute("n", new RecordEntity("", "Сейчас", ""));
        model.addAttribute("nameFolder", new StringEntity(f2.getName()));
        model.addAttribute("userName", principal.getName());

        return "notepad";
    }

    @PostMapping("/notepad/addRecord")
    public String recordAdd(@ModelAttribute("n") RecordEntity n,
                            @RequestParam(value = "id1") int id1,
                            Principal principal,
                            Model model) {
        Date date = new Date();
        UserEntity user = repository.findByName(principal.getName());
        NoteEntity now = new NoteEntity("", date, n.getRecord());
        if (n.getName().equals("")) {
            now.setName("Без имени");
        } else {
            now.setName(n.getName());
        }
        FolderEntity f2 = folderRepository.findById(id1);
        if ((f2 == null) || (f2.getUser() != user)) {
            return "nope";
        }
        now.setFolder(f2);
        noteRepository.save(now);
        String url = "http://localhost:8080/notepad?id1=" + id1 + "&id2=" + now.getId();
        return "redirect:" + url;
    }

    @RequestMapping("/notepad/addFolder")
    public String notepadAddFolder(Principal principal,
                                   Model model) {
        UserEntity user = repository.findByName(principal.getName());

        Set<FolderEntity> folder = user.getFolder();
        List<FolderEntity> folderList = new ArrayList<>();
        folderList.addAll(folder);
        Collections.sort(folderList, FolderEntity.COMPARE_BY_NAME);
        model.addAttribute("folder", folderList);

        model.addAttribute("userName", principal.getName());
        model.addAttribute("d", new DivEntity("AddFolder"));
        model.addAttribute("s", new StringEntity(""));
        model.addAttribute("n", new StringEntity(""));
        FolderEntity f1 = folderRepository.findByUserAndStanding(user,true);
        if (f1 != null) {
            f1.setStanding(false);
            folderRepository.save(f1);
        }

        return "notepad";
    }

    @PostMapping("/notepad/addFolder")
    public String folderAdd(@ModelAttribute("n") StringEntity n,
                            Principal principal,
                            Model model) {
        UserEntity user = repository.findByName(principal.getName());

        FolderEntity now = new FolderEntity(n.getName());
        now.setUser(user);
        folderRepository.save(now);

        String url = "http://localhost:8080/notepad?id1=" + now.getId();
        return "redirect:" + url;
    }

    @PostMapping("/notepad/search")
    public String notepadSearch(HttpServletRequest request,
                                @ModelAttribute("s") StringEntity s,
                                Principal principal,
                                Model model) {
        UserEntity user = repository.findByName(principal.getName());

        String ss = request.getParameter("search");
        FolderEntity f1 = folderRepository.findByUserAndStanding(user,true);
        if (f1 != null) {
            f1.setStanding(false);
            folderRepository.save(f1);
        }
        NoteEntity n1 = noteRepository.findByStandingAndFolder_UserIs(true, user);
        if (n1 != null) {
            n1.setStanding(false);
            noteRepository.save(n1);
        }

        DivEntity d = new DivEntity("Search");
        if (ss.equals("folderName")) {
            d.setNote();
            model.addAttribute("request", "групп c именем " + "\""+s.getName()+"\"");
            List<FolderEntity> folderListSearch = folderRepository.findByUserAndNameContaining(user, s.getName());
            if (folderListSearch.isEmpty()) {
                d.setDivSearch();
            } else {
                Collections.sort(folderListSearch, FolderEntity.COMPARE_BY_NAME);
                model.addAttribute("folderSearch", folderListSearch);
            }
        } else {
            List<NoteEntity> noteList;
            if (ss.equals("noteName")) {
                model.addAttribute("request", "записей с именем " + "\""+s.getName()+"\"");
                noteList = noteRepository.findByNameContainingAndFolder_UserIs(s.getName(), user);
            } else {
                model.addAttribute("request", "записей, содержащих " + "\""+s.getName()+"\"");
                noteList = noteRepository.findByRecordContainingAndFolder_UserIs(s.getName(), user);
            }
            if (noteList.isEmpty()) {
                d.setDivSearch();
            } else {
                noteList.sort(Comparator.comparing(NoteEntity::getName).thenComparing(NoteEntity::getDate));
                model.addAttribute("note", noteList);
            }
        }

        Set<FolderEntity> folder = user.getFolder();
        List<FolderEntity> folderList = new ArrayList<>();
        folderList.addAll(folder);
        Collections.sort(folderList, FolderEntity.COMPARE_BY_NAME);
        model.addAttribute("folder", folderList);

        model.addAttribute("d", d);
        model.addAttribute("s", new StringEntity(""));
        model.addAttribute("userName", principal.getName());
        return "notepad";
    }

    @GetMapping("/notepad/search")
    public String notepadSearchUrl(@RequestParam(value = "id1", required = false, defaultValue = "-1") int id1,
                                   @RequestParam(value = "id2", required = false, defaultValue = "-1") int id2,
                                   Principal principal,
                                   Model model) {
        String url = "http://localhost:8080/notepad?id1=";
        if (id1 >= 0) {
             url = url + id1;
        } else {
            NoteEntity n = noteRepository.findById(id2);
            if ((n == null) || (!n.getFolder().getUser().getName().equals(principal.getName()))) {
                return "nope";
            }
            long id = n.getFolder().getId();
            url = url + id + "&id2=" + id2;
            FolderEntity f = folderRepository.findById(id);
            if ((f == null) || (!f.getUser().getName().equals(principal.getName()))) {
                return "nope";
            }
            f.setStanding(true);
            folderRepository.save(f);
        }
        return "redirect:" + url;
    }

    @RequestMapping("/notepad/delRecord")
    public String notepadDelRecord(@RequestParam(value = "id1") int id1,
                                   @RequestParam(value = "id2") int id2,
                                   Principal principal,
                                   Model model) {

        FolderEntity f = folderRepository.findById(id1);
        if ((f == null) || (!f.getUser().getName().equals(principal.getName()))) {
            return "nope";
        }
        NoteEntity n = noteRepository.findById(id2);
        if ((n == null) || (!n.getFolder().getUser().getName().equals(principal.getName()))) {
            return "nope";
        }
        f.removeNote(n);
        f.setStanding(false);
        folderRepository.save(f);
        String url = "http://localhost:8080/notepad?id1=" + id1;
        return "redirect:" + url;
    }

    @RequestMapping("/notepad/delFolder")
    public String notepadDelFolder(@RequestParam(value = "id1") int id1,
                                   Principal principal,
                                   Model model) {

        UserEntity user = repository.findByName(principal.getName());
        FolderEntity f = folderRepository.findById(id1);
        if ((f == null) || (!f.getUser().getName().equals(principal.getName()))) {
            return "nope";
        }
        user.removeFolder(f);
        repository.save(user);
        return "redirect:http://localhost:8080/notepad";
    }
}