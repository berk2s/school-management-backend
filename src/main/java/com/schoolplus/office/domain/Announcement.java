package com.schoolplus.office.domain;

import com.schoolplus.office.web.models.AnnouncementChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "announcement_title")
    private String announcementTitle;

    @Column(name = "announcement_description", length = 5000)
    private String announcementDescription;

    @ElementCollection
    @CollectionTable(name = "announcement_channel", joinColumns = @JoinColumn(name = "announcement_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_name")
    private List<AnnouncementChannel> announcementChannels = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "image",
            joinColumns = @JoinColumn(name = "announcement_id"))
    @CollectionId(
            columns = @Column(name = "image_id"),
            type = @Type(type = "long"),
            generator = "sequence")
    @Column(name = "imageUrl")
    private List<String> announcementImages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    private Organization organization;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp lastModifiedAt;

    public void addImage(String imageUrl) {
        if (!announcementImages.contains(imageUrl)) {
            this.announcementImages.add(imageUrl);
        }
    }

    public void removeImage(String imageUrl) {
        if (announcementImages.contains(imageUrl)) {
            this.announcementImages.remove(imageUrl);
        }
    }

    public void addChannel(AnnouncementChannel announcementChannel) {
        if (!announcementChannels.contains(announcementChannel)) {
            this.announcementChannels.add(announcementChannel);
        }
    }

    public void removeChannel(AnnouncementChannel announcementChannel) {
        if (announcementChannels.contains(announcementChannel)) {
            this.announcementChannels.remove(announcementChannel);
        }
    }

}
