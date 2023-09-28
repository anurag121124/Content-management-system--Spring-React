package com.blogapp.api.services.impl;

import com.blogapp.api.entities.Category;
import com.blogapp.api.entities.Post;
import com.blogapp.api.entities.User;
import com.blogapp.api.exception.ResourceNotFoundException;
import com.blogapp.api.payloads.PostDto;
import com.blogapp.api.payloads.PostResponse;
import com.blogapp.api.repositories.CategoryRepo;
import com.blogapp.api.repositories.PostRepo;
import com.blogapp.api.repositories.UserRepo;
import com.blogapp.api.services.PostService;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

@Service
public class PostServiceImpl implements PostService {

        @Autowired
        private PostRepo postRepo;

        @Autowired
        private ModelMapper modelMapper;

        @Autowired
        private UserRepo userRepo;

        @Autowired
        private CategoryRepo categoryRepo;

        @Override
        public PostDto createPost(
                        PostDto postDto,
                        Integer userId,
                        Integer categoryId) {
                User user = this.userRepo.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));

                Category category = this.categoryRepo.findById(categoryId)
                                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id ",
                                                categoryId));

                Post post = this.modelMapper.map(postDto, Post.class);
                post.setImageName("default.png");
                post.setAddedDate(new Date());
                post.setUser(user);
                post.setCategory(category);

                Post newPost = this.postRepo.save(post);

                return this.modelMapper.map(newPost, PostDto.class);
        }

        @Override
        public PostDto updatePost(PostDto postDto, Integer postId) {
                Post post = this.postRepo.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));

                post.setTitle(postDto.getTitle());
                post.setContent(postDto.getContent());
                post.setImageName(postDto.getImageName());
                Post updatePost = this.postRepo.save(post);
                return this.modelMapper.map(updatePost, PostDto.class);
        }

        @Override
        public void deletePost(Integer postId) {

                Post post = this.postRepo.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "Post Id", postId));
                this.postRepo.delete(post);
        }

        @Override
        public PostResponse getAllPosts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
                Sort sort = null;
                if (sortDir.equalsIgnoreCase("desc")) {
                        sort = Sort.by(sortBy).descending();
                } else

                {
                        sort = Sort.by(sortBy).ascending();
                }
                Pageable p = PageRequest.of(pageNumber, pageSize, sort);

                Page<Post> pagePost = this.postRepo.findAll(p);
                List<Post> allPosts = pagePost.getContent();
                List<PostDto> postDtos = allPosts
                                .stream()
                                .map(post -> this.modelMapper.map(post, PostDto.class))
                                .collect(Collectors.toList());
                PostResponse postResponse = new PostResponse();
                postResponse.setContent(postDtos);
                postResponse.setPageNumber(pagePost.getNumber());
                postResponse.setPageSize(pagePost.getSize());
                postResponse.setTotalElements(pagePost.getTotalElements());
                postResponse.setTotalPages(pagePost.getTotalPages());
                postResponse.setLastPage(pagePost.isLast());

                return postResponse;
        }

        @Override
        public PostDto getPostById(Integer postId) {
                Post post = this.postRepo.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("Post", "post id", postId));
                return this.modelMapper.map(post, PostDto.class);
        }

        @Override
        public List<PostDto> getPostsByCategory(Integer categoryId) {
                Category cat = this.categoryRepo.findById(categoryId)
                                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id",
                                                categoryId));
                List<Post> posts = this.postRepo.findByCategory(cat);

                List<PostDto> postDtos = posts
                                .stream()
                                .map(post -> this.modelMapper.map(post, PostDto.class))
                                .collect(Collectors.toList());

                return postDtos;
        }

        @Override
        public List<PostDto> getPostsByUser(Integer userId) {
                User user = this.userRepo.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
                List<Post> posts = this.postRepo.findByUser(user);

                List<PostDto> postDtos = posts
                                .stream()
                                .map(post -> this.modelMapper.map(post, PostDto.class))
                                .collect(Collectors.toList());

                return postDtos;
        }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        List<Post> posts = this.postRepo.searchByTitle("%" + keyword + "%");
        List<PostDto> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
        return postDtos;
    }
}